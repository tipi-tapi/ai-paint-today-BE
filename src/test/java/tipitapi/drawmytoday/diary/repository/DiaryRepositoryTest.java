package tipitapi.drawmytoday.diary.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.util.ReflectionTestUtils;
import tipitapi.drawmytoday.diary.domain.Diary;
import tipitapi.drawmytoday.emotion.domain.Emotion;
import tipitapi.drawmytoday.emotion.repository.EmotionRepository;
import tipitapi.drawmytoday.user.domain.SocialCode;
import tipitapi.drawmytoday.user.domain.User;
import tipitapi.drawmytoday.user.repository.UserRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class DiaryRepositoryTest {

    @Autowired
    DiaryRepository diaryRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    EmotionRepository emotionRepository;

    private User createUser() {
        User user = User.create(SocialCode.GOOGLE);
        userRepository.save(user);
        return user;
    }

    private Diary createDiary(Long diaryId, User user) {
        Emotion emotion = Emotion.create("HAPPY", "#12312", true, "example emotion prompt",
            "example color prompt");
        emotionRepository.save(emotion);

        Diary diary = Diary.builder().user(user).emotion(emotion)
            .diaryDate(LocalDateTime.now()).isAi(true).build();
        ReflectionTestUtils.setField(diary, "diaryId", diaryId);
        diaryRepository.save(diary);

        return diary;
    }

    @Nested
    @DisplayName("findByDiaryIdAndUser 메소드 테스트")
    class findByDiaryIdAndUserTest {

        @Nested
        @DisplayName("유저 소유의 주어진 일기가 존재할 경우")
        class if_diary_of_user_exists {

            @Test
            @DisplayName("일기를 반환한다.")
            void return_diary() {
                User user = createUser();
                Diary diary = createDiary(1L, user);

                Optional<Diary> foundDiary = diaryRepository.findByDiaryIdAndUser(1L, user);

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
                User user = createUser();

                Optional<Diary> foundDiary = diaryRepository.findByDiaryIdAndUser(1L, user);

                assertThat(foundDiary).isEmpty();
            }
        }

        @Nested
        @DisplayName("주어진 user의 일기가 아니면")
        class if_diary_not_owned_user {

            @Test
            @DisplayName("null을 반환한다.")
            void return_null() {
                User user = createUser();
                Diary diary = createDiary(1L, user);
                User otherUser = createUser();

                Optional<Diary> foundDiary = diaryRepository.findByDiaryIdAndUser(1L,
                    otherUser);

                assertThat(foundDiary).isEmpty();
            }
        }
    }
}