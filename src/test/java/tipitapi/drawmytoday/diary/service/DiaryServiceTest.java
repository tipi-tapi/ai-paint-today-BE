package tipitapi.drawmytoday.diary.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import tipitapi.drawmytoday.diary.domain.Diary;
import tipitapi.drawmytoday.diary.domain.Image;
import tipitapi.drawmytoday.diary.dto.GetDiaryResponse;
import tipitapi.drawmytoday.diary.exception.DiaryNotFoundException;
import tipitapi.drawmytoday.diary.exception.ImageNotFoundException;
import tipitapi.drawmytoday.diary.repository.DiaryRepository;
import tipitapi.drawmytoday.emotion.domain.Emotion;
import tipitapi.drawmytoday.user.domain.SocialCode;
import tipitapi.drawmytoday.user.domain.User;

@ExtendWith(MockitoExtension.class)
class DiaryServiceTest {

    @Mock
    DiaryRepository diaryRepository;
    @Mock
    ImageService imageService;
    @Mock
    Image image;
    @InjectMocks
    DiaryService diaryService;

    private User createUser() {
        return User.create(SocialCode.GOOGLE);
    }

    private Diary createDiary(Long diaryId, User user) {
        Emotion emotion = Emotion.create("HAPPY", "#12312", true, "example emotion prompt",
            "example color prompt");
        Diary diary = Diary.builder().user(user).emotion(emotion)
            .diaryDate(LocalDateTime.now()).isAi(true).build();
        ReflectionTestUtils.setField(diary, "diaryId", diaryId);
        return diary;
    }

    private Image createImage(Diary diary) {
        return Image.create(diary, "https://example.com/image.jpg", true);
    }

    @Nested
    @DisplayName("getDiary 메소드 테스트")
    class GetDairyTest {

        @Nested
        @DisplayName("유저 소유의 주어진 일기가 존재할 경우")
        class if_diary_of_user_exists {

            @Test
            @DisplayName("일기를 반환한다.")
            void it_returns_diary() {
                // given
                User user = createUser();
                Diary diary = createDiary(1L, user);
                Image image = createImage(diary);

                given(diaryRepository.findByDiaryIdAndUser(1L, user)).willReturn(
                    Optional.of(diary));
                given(imageService.getImage(diary)).willReturn(image);

                // when
                GetDiaryResponse getDiaryResponse = diaryService.getDiary(user, 1L);

                // then
                assertThat(getDiaryResponse.getId()).isEqualTo(diary.getDiaryId());
            }
        }

        @Nested
        @DisplayName("주어진 일기가 없거나, 유저의 일기가 아닌 경우")
        class if_diary_not_exists_or_not_user_owned_diary {

            @Test
            @DisplayName("예외를 발생시킨다.")
            void it_throws_exception() {
                // given
                User user = createUser();

                given(diaryRepository.findByDiaryIdAndUser(1L, user)).willReturn(Optional.empty());

                // when & then
                assertThatThrownBy(() -> diaryService.getDiary(user, 1L))
                    .isInstanceOf(DiaryNotFoundException.class);
            }
        }

        @Nested
        @DisplayName("일기에 이미지가 존재하지 않는 경우")
        class if_diary_has_no_image {

            @Test
            @DisplayName("예외를 발생시킨다.")
            void it_throws_exception() {
                // given
                User user = createUser();
                Diary diary = createDiary(1L, user);

                given(diaryRepository.findByDiaryIdAndUser(1L, user)).willReturn(
                    Optional.of(diary));
                given(imageService.getImage(diary)).willThrow(ImageNotFoundException.class);

                // when & then
                assertThatThrownBy(() -> diaryService.getDiary(user, 1L))
                    .isInstanceOf(ImageNotFoundException.class);
            }
        }
    }
}