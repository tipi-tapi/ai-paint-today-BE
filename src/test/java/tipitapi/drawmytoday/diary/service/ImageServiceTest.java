package tipitapi.drawmytoday.diary.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static tipitapi.drawmytoday.common.testdata.TestDiary.createDiary;
import static tipitapi.drawmytoday.common.testdata.TestDiary.createDiaryWithId;
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
import tipitapi.drawmytoday.s3.service.S3Service;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @Mock
    ImageRepository imageRepository;
    @Mock
    S3Service s3Service;
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

    @Nested
    @DisplayName("createImage 메서드는")
    class CreateImageTest {

        @Test
        @DisplayName("이미지를 생성한다.")
        void it_creates_image() {
            // given
            Diary diary = createDiary(createUser(), createEmotion());
            Image image = createImageWithId(1L, diary);

            given(imageRepository.save(any(Image.class))).willReturn(image);

            // when
            Image createdImage = imageService.createImage(diary, "post/1/1234_1.png", true);

            // then
            assertThat(createdImage).isEqualTo(image);

            verify(imageRepository).save(any(Image.class));
        }
    }

    @Nested
    @DisplayName("uploadImage 메서드는")
    class UploadImageTest {

        @Test
        @DisplayName("이미지를 업로드하고 생성한다")
        void it_uploads_and_creates_image() {
            // given
            Long diaryId = 1L;
            Diary diary = createDiaryWithId(diaryId, createUser(), createEmotion());
            Image image = createImageWithId(1L, diary);
            String imagePathRegex = "post/" + diaryId + "/\\d+_1.png";

            given(imageRepository.save(any(Image.class))).willReturn(image);

            // when
            Image createdImage = imageService.uploadImage(diary, new byte[1], true);

            // then
            assertThat(createdImage).isEqualTo(image);
            verify(s3Service).uploadImage(any(byte[].class), matches(imagePathRegex));
            verify(imageRepository).save(any(Image.class));
        }
    }
}