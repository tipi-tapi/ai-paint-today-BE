package tipitapi.drawmytoday.diary.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static tipitapi.drawmytoday.common.testdata.TestDiary.createDiaryWithCreatedAt;
import static tipitapi.drawmytoday.common.testdata.TestDiary.createDiaryWithDate;
import static tipitapi.drawmytoday.common.testdata.TestUser.createUserWithId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.context.jdbc.Sql;
import tipitapi.drawmytoday.common.BaseRepositoryTest;
import tipitapi.drawmytoday.common.config.QuerydslConfig;
import tipitapi.drawmytoday.common.testdata.TestDiary;
import tipitapi.drawmytoday.common.testdata.TestEmotion;
import tipitapi.drawmytoday.common.testdata.TestImage;
import tipitapi.drawmytoday.common.testdata.TestUser;
import tipitapi.drawmytoday.common.utils.DateUtils;
import tipitapi.drawmytoday.domain.admin.dto.GetDiaryAdminResponse;
import tipitapi.drawmytoday.domain.diary.domain.Diary;
import tipitapi.drawmytoday.domain.diary.repository.DiaryRepository;
import tipitapi.drawmytoday.domain.emotion.domain.Emotion;
import tipitapi.drawmytoday.domain.user.domain.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import(QuerydslConfig.class)
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
    @DisplayName("getDiariesForMonitorAsPage 메소드 테스트")
    class GetDiariesForMonitorAsPageTest {
        // TODO: Diary 도메인의 @SQLDelete에 따른 @Where Clause 적용으로인해, queryDSL 기반 메서드임에도 삭제된 일기가 쿼리에서 제외됨. 그래서 아래 테스트 중 일부가 실패함.
        //  따라서, diary 관련 레포지토리 메서드를 queryDSL 기반으로 변경한 후 @Where Clause 설정을 삭제해 개선해야함

        @Nested
        @DisplayName("삭제된 일기가 있을 경우")
        class if_deleted_diary_exist {

            @Test
            @DisplayName("삭제된 일기를 포함한 일기 리스트를 반환한다.")
            @Sql("GetDiariesForMonitorAsPageTest.sql")
            void return_diary_list_includes_deleted() {
                int page = 0;
                int size = 5;
                Page<GetDiaryAdminResponse> response = diaryRepository.getDiariesForMonitorAsPage(
                    Pageable.ofSize(size).withPage(page), Direction.DESC, null);

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
            @Sql("GetDiariesForMonitorAsPageTest.sql")
            void return_diary_list_excludes_dummy_image() {
                User user = userRepository.save(TestUser.createUser());
                Emotion emotion = emotionRepository.save(TestEmotion.createEmotion());
                Diary diary = diaryRepository.save(TestDiary.createTestDiary(user, emotion));
                imageRepository.save(TestImage.createImage(diary));

                int page = 0;
                int size = 5;
                Page<GetDiaryAdminResponse> response = diaryRepository.getDiariesForMonitorAsPage(
                    Pageable.ofSize(size).withPage(page), Direction.DESC, null);

                assertThat(response.get()
                    .filter(
                        diaryResponse -> Objects.equals(diaryResponse.getId(), diary.getDiaryId()))
                    .findAny()).isEmpty();
            }
        }

        @Nested
        @DisplayName("감정 ID가 주어졌을 경우")
        class if_emotion_id_given {

            @Test
            @DisplayName("해당 감정이 존재한다면, 필터링한다.")
            @Sql("GetDiariesForMonitorAsPageTest.sql")
            void return_diary_list_with_emotion_filtered() {
                int page = 0;
                int size = 5;
                Page<GetDiaryAdminResponse> response = diaryRepository.getDiariesForMonitorAsPage(
                    Pageable.ofSize(size).withPage(page), Direction.DESC, 1L);

                assertThat(response.getTotalElements()).isEqualTo(5);
                assertThat(response.getContent().size()).isEqualTo(5);
                assertThat(response.getTotalPages()).isEqualTo(1);
                assertThat(response.getSort().isSorted()).isTrue();
                assertThat(response.getContent().get(0).getId()).isEqualTo(5L);
            }

            @Test
            @DisplayName("해당 감정이 존재하지 않는다면, 필터링을 적용하지 않는다.")
            @Sql("GetDiariesForMonitorAsPageTest.sql")
            void return_diary_list_without_emotion_filtered() {
                int page = 0;
                int size = 5;
                Page<GetDiaryAdminResponse> response = diaryRepository.getDiariesForMonitorAsPage(
                    Pageable.ofSize(size).withPage(page), Direction.DESC, null);

                assertThat(response.getTotalElements()).isEqualTo(10);
                assertThat(response.getContent().size()).isEqualTo(5);
                assertThat(response.getTotalPages()).isEqualTo(2);
                assertThat(response.getSort().isSorted()).isTrue();
                assertThat(response.getContent().get(0).getId()).isEqualTo(10L);
            }
        }
    }

    @Nested
    @DisplayName("getDiaryExistsByDiaryDate 메서드 테스트")
    class GetDiaryExistsByDiaryDateTest {

        private final LocalDate DIARY_DATE = LocalDate.of(2021, 1, 1);

        @Nested
        @DisplayName("해당 날짜에 일기가 없을 경우")
        class if_diary_not_exist_at_date {

            @Test
            @DisplayName("null을 반환한다.")
            void return_null() {
                assertThat(diaryRepository.getDiaryExistsByDiaryDate(createUser().getUserId(),
                    DIARY_DATE)).isEmpty();
            }
        }

        @Nested
        @DisplayName("존재하지 않는 유저 ID인 경우")
        class if_user_id_not_exist {

            @Test
            @DisplayName("null을 반환한다.")
            void return_null() {
                assertThat(diaryRepository.getDiaryExistsByDiaryDate(1L, DIARY_DATE)).isEmpty();
            }
        }

        @Nested
        @DisplayName("해당 날짜에 주어진 유저의 일기가 있을 경우")
        class if_diary_exists {

            @Test
            @DisplayName("일기를 반환한다.")
            void return_diary() {
                User user = createUser();
                Diary diary = diaryRepository.save(createDiaryWithDate(DIARY_DATE.atTime(9, 0),
                    user, createEmotion()));

                assertThat(diaryRepository.getDiaryExistsByDiaryDate(user.getUserId(), DIARY_DATE))
                    .isPresent()
                    .get()
                    .isEqualTo(diary);
            }
        }
    }
}