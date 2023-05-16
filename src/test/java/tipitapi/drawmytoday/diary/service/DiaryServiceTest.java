package tipitapi.drawmytoday.diary.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import tipitapi.drawmytoday.diary.domain.Diary;
import tipitapi.drawmytoday.diary.domain.Image;
import tipitapi.drawmytoday.diary.dto.GetDiaryResponse;
import tipitapi.drawmytoday.diary.exception.DiaryNotFoundException;
import tipitapi.drawmytoday.diary.repository.DiaryRepository;
import tipitapi.drawmytoday.emotion.domain.Emotion;
import tipitapi.drawmytoday.user.domain.SocialCode;
import tipitapi.drawmytoday.user.domain.User;

class DiaryServiceTest {

    private final DiaryRepository diaryRepository = Mockito.mock(DiaryRepository.class);
    private final ImageService imageService = Mockito.mock(ImageService.class);
    private final DiaryService diaryService = new DiaryService(diaryRepository, imageService);

    @Test
    @DisplayName("특정 일기 조회")
    void get_diary() {
        User user = User.create(SocialCode.APPLE);
        Emotion emotion = Emotion.create("HAPPY", "#12312", true, "example emotion prompt",
            "example color prompt");
        Diary diary = Diary.builder().user(user).emotion(emotion).diaryDate(LocalDateTime.now())
            .isAi(true).build();
        Image image = Image.create(diary, "https://example.com/image.jpg", true);

        ReflectionTestUtils.setField(diary, "diaryId", 1L);
        given(diaryRepository.findByIdAndUser(1L, user)).willReturn(Optional.of(diary));
        given(imageService.getImage(diary)).willReturn(image);

        GetDiaryResponse getDiaryResponse = diaryService.getDiary(user, 1L);

        assertThat(getDiaryResponse.getId()).isEqualTo(diary.getDiaryId());
        assertThat(getDiaryResponse.getImageUrl()).isEqualTo(image.getImageUrl());
    }

    @Test
    @DisplayName("주어진 일기가 존재하지 않을 경우, 예외가 발생한다.")
    void get_diary_not_found() {
        User user = User.create(SocialCode.APPLE);

        given(diaryRepository.findByIdAndUser(1L, user)).willReturn(Optional.empty());

        assertThatThrownBy(() -> diaryService.getDiary(user, 1L))
            .isInstanceOf(DiaryNotFoundException.class);
    }
}