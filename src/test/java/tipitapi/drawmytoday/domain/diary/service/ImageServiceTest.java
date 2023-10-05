package tipitapi.drawmytoday.domain.diary.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static tipitapi.drawmytoday.common.testdata.TestDiary.createDiary;
import static tipitapi.drawmytoday.common.testdata.TestDiary.createDiaryWithId;
import static tipitapi.drawmytoday.common.testdata.TestEmotion.createEmotion;
import static tipitapi.drawmytoday.common.testdata.TestImage.createImage;
import static tipitapi.drawmytoday.common.testdata.TestImage.createImageWithId;
import static tipitapi.drawmytoday.common.testdata.TestUser.createUser;
import static tipitapi.drawmytoday.common.testdata.TestUser.createUserWithId;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import tipitapi.drawmytoday.domain.diary.domain.Diary;
import tipitapi.drawmytoday.domain.diary.domain.Image;
import tipitapi.drawmytoday.domain.diary.exception.DiaryNeedsImageException;
import tipitapi.drawmytoday.domain.diary.exception.ImageNotFoundException;
import tipitapi.drawmytoday.domain.diary.exception.SelectedImageDeletionDeniedException;
import tipitapi.drawmytoday.domain.diary.repository.ImageRepository;
import tipitapi.drawmytoday.domain.r2.service.R2Service;
import tipitapi.drawmytoday.domain.user.domain.User;
import tipitapi.drawmytoday.domain.user.service.ValidateUserService;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @Mock
    ImageRepository imageRepository;
    @Mock
    R2Service r2Service;
    @Mock
    ValidateUserService validateUserService;
    @Mock
    ValidateDiaryService validateDiaryService;
    @Mock
    ValidateImageService validateImageService;
    @InjectMocks
    ImageService imageService;

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
    @DisplayName("uploadAndCreateImage 메서드는")
    class UploadAndCreateImageTest {

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
            Image createdImage = imageService.uploadAndCreateImage(diary, new byte[1], true);

            // then
            assertThat(createdImage).isEqualTo(image);
            verify(r2Service).uploadImage(any(byte[].class), matches(imagePathRegex));
            verify(imageRepository).save(any(Image.class));
        }
    }

    @Nested
    @DisplayName("deleteImage 메서드는")
    class DeleteImageTest {

        @Nested
        @DisplayName("이미지가 존재하지 않으면")
        class if_image_not_found {

            @Test
            @DisplayName("ImageNotFoundException 예외를 반환한다.")
            void it_throws_SelectedImageDeletionException() {
                User user = createUser();

                given(validateUserService.validateUserById(anyLong())).willReturn(user);
                given(imageRepository.findImage(anyLong())).willReturn(Optional.empty());

                assertThatThrownBy(() -> imageService.deleteImage(1L, 1L))
                    .isInstanceOf(ImageNotFoundException.class);
            }
        }

        @Nested
        @DisplayName("이미지가 대표 이미지라면")
        class if_image_is_selected {

            @Test
            @DisplayName("SelectedImageDeletionException 예외를 반환한다.")
            void it_throws_SelectedImageDeletionException() {
                User user = createUser();
                Diary diary = createDiary(createUser(), createEmotion());
                Image image = createImage(diary);
                ReflectionTestUtils.setField(image, "isSelected", true);

                given(validateUserService.validateUserById(anyLong())).willReturn(user);
                given(imageRepository.findImage(anyLong())).willReturn(Optional.of(image));

                assertThatThrownBy(() -> imageService.deleteImage(1L, 1L))
                    .isInstanceOf(SelectedImageDeletionDeniedException.class);
            }
        }

        @Nested
        @DisplayName("일기의 이미지가 1개 이하라면")
        class if_image_count_is_less_than_one {

            @Test
            @DisplayName("DiaryNeedsImageException 예외를 반환한다.")
            void it_throws_DiaryNeedsImageException() {
                User user = createUser();
                Diary diary = createDiaryWithId(1L, createUser(), createEmotion());
                Image image = createImage(diary);
                ReflectionTestUtils.setField(image, "isSelected", false);

                given(validateUserService.validateUserById(anyLong())).willReturn(user);
                given(imageRepository.findImage(anyLong())).willReturn(Optional.of(image));
                given(imageRepository.countImage(anyLong())).willReturn(1L);

                assertThatThrownBy(() -> imageService.deleteImage(1L, 1L))
                    .isInstanceOf(DiaryNeedsImageException.class);
            }
        }
    }

    @Nested
    @DisplayName("reviewImage 메소드 테스트")
    class ReviewImageTest {

        @Test
        @DisplayName("userId와 imageId 검증 이후 review를 업데이트한다.")
        void it_updates_image_review() {
            User user = createUserWithId(1L);
            Diary diary = createDiaryWithId(1L, user, createEmotion());
            Image image = createImageWithId(1L, diary);
            String review = "5";
            given(validateUserService.validateUserById(any(Long.class))).willReturn(user);
            given(validateImageService.validateImageById(any(Long.class))).willReturn(image);

            imageService.reviewImage(1L, 1L, review);

            assertThat(image.getReview()).isEqualTo(review);
            verify(validateImageService).validateImageOwner(eq(image.getImageId()), eq(user));
        }
    }
}