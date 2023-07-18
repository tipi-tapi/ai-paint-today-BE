package tipitapi.drawmytoday.diary.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static tipitapi.drawmytoday.common.testdata.TestDiary.createDiaryWithCreatedAt;
import static tipitapi.drawmytoday.common.testdata.TestDiary.createDiaryWithDate;
import static tipitapi.drawmytoday.common.testdata.TestUser.createUserWithId;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.context.jdbc.Sql;
import tipitapi.drawmytoday.common.BaseRepositoryTest;
import tipitapi.drawmytoday.common.testdata.TestDiary;
import tipitapi.drawmytoday.common.testdata.TestEmotion;
import tipitapi.drawmytoday.common.testdata.TestUser;
import tipitapi.drawmytoday.common.utils.DateUtils;
import tipitapi.drawmytoday.diary.domain.Diary;
import tipitapi.drawmytoday.diary.domain.Image;
import tipitapi.drawmytoday.diary.dto.DiaryForMonitorQueryResponse;
import tipitapi.drawmytoday.emotion.domain.Emotion;
import tipitapi.drawmytoday.user.domain.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class DiaryRepositoryTest extends BaseRepositoryTest {

    @Autowired
    DiaryRepository diaryRepository;

    @Nested
    @DisplayName("findByDiaryId 메소드 테스트")
    class findByDiaryIdAndUserTest {

        @Nested
        @DisplayName("주어진 일기가 존재할 경우")
        class if_diary_of_user_exists {

            @Test
            @DisplayName("일기를 반환한다.")
            void return_diary() {
                Diary diary = createDiary(createUser(), createEmotion());

                Optional<Diary> foundDiary = diaryRepository.findById(diary.getDiaryId());

                assertThat(foundDiary.isPresent()).isTrue();
                assertThat(foundDiary.get().getDiaryId()).isEqualTo(diary.getDiaryId());
            }
        }

        @Nested
        @DisplayName("주어진 id의 일기가 없으면")
        class if_diary_not_exists {

            @Test
            @DisplayName("null을 반환한다.")
            void return_null() {
                Optional<Diary> foundDiary = diaryRepository.findById(1L);

                assertThat(foundDiary).isEmpty();
            }
        }
    }

    @Nested
    @DisplayName("findAllByUserUserIdAndDiaryDateBetween 메소드 테스트")
    class findAllByUserUserIdAndDiaryDateBetweenTest {

        private final LocalDateTime START_DATE = DateUtils.getStartDate(2023, 6);
        private final LocalDateTime END_DATE = DateUtils.getStartDate(2023, 6);

        @Nested
        @DisplayName("userId에 해당하는 유저가 없을 경우")
        class if_user_not_exist {

            @Test
            @DisplayName("빈 리스트를 반환한다.")
            void return_empty_list() {
                assertThat(
                    diaryRepository.findAllByUserUserIdAndDiaryDateBetween(
                        1L, START_DATE, END_DATE)
                ).isEmpty();
            }
        }

        @Nested
        @DisplayName("userId에 해당하는 유저가 있으나 월에 해당하는 일기가 없을 경우")
        class if_user_exist_but_diary_not_exist {

            @Test
            @DisplayName("빈 리스트를 반환한다.")
            void return_empty_list() {
                LocalDateTime diaryDate = LocalDateTime.of(2023, 5, 1, 0, 0);
                User user = createUser();
                Emotion emotion = createEmotion();
                diaryRepository.save(createDiaryWithDate(diaryDate, user, emotion));

                assertThat(
                    diaryRepository.findAllByUserUserIdAndDiaryDateBetween(
                        user.getUserId(), START_DATE, END_DATE))
                    .isEmpty();
            }
        }

        @Nested
        @DisplayName("userId에 해당하는 유저가 있고 월에 해당하는 일기가 있을 경우")
        class if_user_exist_and_diary_exist {

            @Test
            @DisplayName("해당 월의 일기 리스트를 반환한다.")
            void return_diary_list() {
                LocalDateTime diaryDate = LocalDateTime.of(2023, 6, 1, 0, 0);
                User user = createUser();
                Emotion emotion = createEmotion();
                diaryRepository.saveAll(
                    List.of(createDiaryWithDate(diaryDate, user, emotion),
                        createDiaryWithDate(diaryDate, user, emotion)));

                List<Diary> diaryList = diaryRepository.findAllByUserUserIdAndDiaryDateBetween(
                    user.getUserId(), START_DATE, END_DATE);
                assertThat(diaryList.size()).isEqualTo(2);
            }

            @Nested
            @DisplayName("삭제된 일기가 있을 경우")
            class if_deleted_diary_exist {

                @Test
                @DisplayName("삭제된 일기는 포함하지 않은 일기 리스트를 반환한다.")
                void return_diary_list_without_deleted() {
                    LocalDateTime diaryDate = LocalDateTime.of(2023, 6, 1, 0, 0);
                    User user = createUser();
                    Emotion emotion = createEmotion();
                    diaryRepository.save(
                        createDiaryWithDate(diaryDate, user, emotion));
                    Diary deletedDiary = diaryRepository.save(
                        createDiaryWithDate(diaryDate, user, emotion));

                    diaryRepository.delete(deletedDiary);

                    List<Diary> diaryList = diaryRepository.findAllByUserUserIdAndDiaryDateBetween(
                        user.getUserId(), START_DATE, END_DATE);
                    assertThat(diaryList.size()).isEqualTo(1);
                    assertThat(diaryList).doesNotContain(deletedDiary);
                }
            }
        }
    }

    @Nested
    @DisplayName("findFirstByUserUserIdOrderByCreatedAtDesc 메소드 테스트")
    class findFirstByUserUserIdOrderByCreatedAtDescTest {

        @Nested
        @DisplayName("주어진 user의 Diary가 존재하지 않을 경우")
        class if_diary_not_exist {

            @Test
            @DisplayName("null를 반환한다.")
            void return_null() {
                Long userId = 1L;
                createUserWithId(userId);

                assertThat(
                    diaryRepository.findFirstByUserUserIdOrderByCreatedAtDesc(userId))
                    .isNotPresent();
            }
        }

        @Nested
        @DisplayName("주어진 user의 Diary가 존재할 경우")
        class if_diary_exist {

            @Test
            @DisplayName("마지막으로 생성한 Diary를 반환한다.")
            void return_diary() {
                User user = createUser();
                Emotion emotion = createEmotion();
                diaryRepository.save(
                    createDiaryWithCreatedAt(LocalDateTime.now().minusDays(2), user, emotion));
                Diary lastDiary = diaryRepository.save(
                    createDiaryWithCreatedAt(LocalDateTime.now().minusDays(1), user,
                        emotion));

                Optional<Diary> diary = diaryRepository.findFirstByUserUserIdOrderByCreatedAtDesc(
                    user.getUserId());

                assertThat(diary.isPresent()).isTrue();
                assertThat(diary.get().getDiaryId()).isEqualTo(lastDiary.getDiaryId());
            }
        }

    }

    @Nested
    @DisplayName("delete 메소드 테스트")
    class DeleteTest {

        @Nested
        @DisplayName("기존에 삭제 처리되지 않은 일기의 경우")
        class if_diary_not_deleted {

            @Test
            @DisplayName("deletedAt에 값을 입력한다.")
            void update_deleted_at() {
                Diary diary = createDiary(createUser(), createEmotion());

                diaryRepository.delete(diary);
                diaryRepository.flush();

                assertThat(diaryRepository.findById(1L)).isNotPresent();

            }
        }
    }

    @Nested
    @DisplayName("getAllDiariesForMonitorAsPage 메소드 테스트")
    class GetAllDiariesForMonitorAsPageTest {

        @Value("${dummy.image.path}")
        private String dummyImageUrl;

        @Nested
        @DisplayName("삭제된 일기가 있을 경우")
        class if_deleted_diary_exist {

            @Test
            @DisplayName("삭제된 일기를 포함한 일기 리스트를 반환한다.")
            @Sql("GetAllDiariesForMonitorAsPageTest.sql")
            void return_diary_list_includes_deleted() {
                int page = 0;
                int size = 5;
                Direction direction = Direction.DESC;
                Page<DiaryForMonitorQueryResponse> response = diaryRepository.getAllDiariesForMonitorAsPage(
                    PageRequest.of(page, size, Sort.by(direction, "created_at", "diary_id")),
                    dummyImageUrl);

                assertThat(response.getTotalElements()).isEqualTo(10);
                assertThat(response.getContent().size()).isEqualTo(5);
                assertThat(response.getTotalPages()).isEqualTo(2);
                assertThat(response.getSort().isSorted()).isTrue();
                assertThat(response.getContent().get(0).getId()).isEqualTo(10L);
            }
        }

        @Nested
        @DisplayName("더미 이미지가 있을 경우")
        class if_dummy_image_exist {

            @Test
            @DisplayName("더미 이미지를 제외한 일기 리스트를 반환한다.")
            @Sql("GetAllDiariesForMonitorAsPageTest.sql")
            void return_diary_list_excludes_dummy_image() {
                User user = userRepository.save(TestUser.createUser());
                Emotion emotion = emotionRepository.save(TestEmotion.createEmotion());
                Diary diary = TestDiary.createDiary(user, emotion);
                Image dummyImage = Image.create(diary, dummyImageUrl, true);
                diaryRepository.save(diary);
                imageRepository.save(dummyImage);

                int page = 0;
                int size = 5;
                Direction direction = Direction.DESC;
                Page<DiaryForMonitorQueryResponse> response = diaryRepository.getAllDiariesForMonitorAsPage(
                    PageRequest.of(page, size, Sort.by(direction, "created_at", "diary_id")),
                    dummyImageUrl);

                assertThat(response.get()
                    .filter(diaryResponse -> Objects.equals(diaryResponse.getImageUrl(),
                        dummyImage.getImageUrl()))
                    .findAny()).isEmpty();
            }
        }
    }

    @Nested
    @DisplayName("findByUserIdAndDiaryDate")
    class FindByUserIdAndDiaryDateTest {

        private final LocalDateTime DIARY_DATE_TIME = LocalDateTime.of(2021, 1, 1, 0, 0);
        private final Date DIARY_DATE = Date.valueOf(DIARY_DATE_TIME.toLocalDate());

        @Nested
        @DisplayName("해당 날짜에 일기가 없을 경우")
        class if_diary_not_exist_at_date {

            @Test
            @DisplayName("빈 리스트를 반환한다.")
            void return_empty_list() {
                assertThat(
                    diaryRepository.findByUserIdAndDiaryDate(createUser().getUserId(), DIARY_DATE))
                    .isEmpty();
            }
        }

        @Nested
        @DisplayName("해당 날짜에 일기가 있을 경우")
        class if_diary_exist_at_date {

            @Nested
            @DisplayName("여러개의 일기가 있을 경우")
            class if_diary_exist_more_than_one {

                @Test
                @DisplayName("최신순으로 해당 날짜의 일기 리스트를 반환한다.")
                void return_diaries_order_by_newest() {
                    User user = createUser();
                    Emotion emotion = createEmotion();
                    Diary prevDiary = diaryRepository.save(
                        createDiaryWithDate(DIARY_DATE_TIME, user, emotion));
                    Diary lastDiary = diaryRepository.save(
                        createDiaryWithDate(DIARY_DATE_TIME, user, emotion));

                    List<Diary> diaries = diaryRepository.findByUserIdAndDiaryDate(user.getUserId(),
                        DIARY_DATE);

                    assertThat(diaries.size()).isEqualTo(2);
                    assertThat(diaries.get(0).getDiaryId()).isEqualTo(lastDiary.getDiaryId());
                    assertThat(diaries.get(1).getDiaryId()).isEqualTo(prevDiary.getDiaryId());
                }
            }

            @Nested
            @DisplayName("한개의 일기만 있을 경우")
            class if_diary_exist_only_one {

                @Test
                @DisplayName("해당 날짜의 일기를 반환한다.")
                void return_diary() {
                    User user = createUser();
                    LocalDateTime diaryDate = LocalDateTime.of(2021, 1, 1, 0, 0);
                    Diary diary = diaryRepository.save(
                        createDiaryWithDate(diaryDate, user, createEmotion()));

                    List<Diary> diaryList = diaryRepository.findByUserIdAndDiaryDate(
                        user.getUserId(), DIARY_DATE);
                    assertThat(diaryList.size()).isEqualTo(1);
                    assertThat(diaryList.get(0).getDiaryId()).isEqualTo(diary.getDiaryId());
                }
            }
        }
    }
}