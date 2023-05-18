package tipitapi.drawmytoday.diary.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static tipitapi.drawmytoday.common.testdata.TestDiary.createDiary;
import static tipitapi.drawmytoday.common.testdata.TestEmotion.createEmotion;
import static tipitapi.drawmytoday.common.testdata.TestImage.createImageWithId;
import static tipitapi.drawmytoday.common.testdata.TestUser.createUser;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tipitapi.drawmytoday.diary.domain.Diary;
import tipitapi.drawmytoday.diary.domain.Image;
import tipitapi.drawmytoday.diary.exception.ImageNotFoundException;
import tipitapi.drawmytoday.diary.repository.ImageRepository;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @Mock
    ImageRepository imageRepository;
    @InjectMocks
    ImageService imageService;

    @Nested
    @DisplayName("getImage 메소드 테스트")
    class GetImageTest {

        @Nested
        @DisplayName("주어진 일기의 이미지 중 선택된 이미지가 존재할 경우")
        class if_selected_image_of_diary_exist {

            @Test
            @DisplayName("이미지를 반환한다.")
            void it_returns_image() {
                Diary diary = createDiary(createUser(), createEmotion());
                Image image = createImageWithId(1L, diary);

                given(imageRepository.findByIsSelectedTrueAndDiary(diary)).willReturn(
                    Optional.of(image));

                Image getImage = imageService.getImage(diary);

                assertThat(getImage.getImageId()).isEqualTo(image.getImageId());
            }
        }

        @Nested
        @DisplayName("주어진 일기의 이미지 중 선택된 이미지가 존재하지 않을 경우")
        class if_selected_image_of_diary_not_exist {

            @Test
            @DisplayName("ImageNotFoundException 예외를 반환한다.")
            void it_throws_ImageNotFoundException() {
                Diary diary = createDiary(createUser(), createEmotion());

                given(imageRepository.findByIsSelectedTrueAndDiary(diary)).willReturn(
                    Optional.empty());

                assertThatThrownBy(() -> imageService.getImage(diary))
                    .isInstanceOf(ImageNotFoundException.class);
            }
        }
    }
}