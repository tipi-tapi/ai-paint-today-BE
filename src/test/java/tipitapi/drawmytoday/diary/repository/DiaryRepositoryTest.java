package tipitapi.drawmytoday.diary.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static tipitapi.drawmytoday.common.testdata.TestDiary.createDiaryWithIdAndDate;
import static tipitapi.drawmytoday.common.testdata.TestEmotion.createEmotionWithId;
import static tipitapi.drawmytoday.common.testdata.TestUser.createUserWithId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import tipitapi.drawmytoday.common.BaseRepositoryTest;
import tipitapi.drawmytoday.common.utils.DateUtils;
import tipitapi.drawmytoday.diary.domain.Diary;
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
                User user = createUser();
                Diary diary = createDiary(1L, user);

                Optional<Diary> foundDiary = diaryRepository.findById(1L);

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
                Long existUserId = 1L;
                Long inputUserId = 2L;
                createUserWithId(existUserId);

                assertThat(
                    diaryRepository.findAllByUserUserIdAndDiaryDateBetween(inputUserId, START_DATE,
                        END_DATE))
                    .isEmpty();
            }
        }

        @Nested
        @DisplayName("userId에 해당하는 유저가 있으나 월에 해당하는 일기가 없을 경우")
        class if_user_exist_but_diary_not_exist {

            @Test
            @DisplayName("빈 리스트를 반환한다.")
            void return_empty_list() {
                LocalDateTime diaryDate = LocalDateTime.of(2023, 5, 1, 0, 0);
                Long userId = 1L;
                User user = createUserWithId(userId);
                Emotion emotion = createEmotionWithId(1L);
                Diary diary = createDiaryWithIdAndDate(1L, diaryDate, user, emotion);
                userRepository.save(user);
                emotionRepository.save(emotion);
                diaryRepository.save(diary);
                createImage(1L, diary);

                assertThat(
                    diaryRepository.findAllByUserUserIdAndDiaryDateBetween(userId, START_DATE,
                        END_DATE))
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
                Long userId = 1L;
                User user = createUserWithId(userId);
                Emotion emotion = createEmotionWithId(1L);
                Diary diary1 = createDiaryWithIdAndDate(1L, diaryDate, user, emotion);
                Diary diary2 = createDiaryWithIdAndDate(2L, diaryDate, user, emotion);
                userRepository.save(user);
                emotionRepository.save(emotion);
                diaryRepository.save(diary1);
                diaryRepository.save(diary2);
                createImage(1L, diary1);
                createImage(2L, diary2);

                List<Diary> diaryList = diaryRepository.findAllByUserUserIdAndDiaryDateBetween(
                    userId, START_DATE, END_DATE);
                assertThat(diaryList.size()).isEqualTo(2);
            }
        }
    }
}