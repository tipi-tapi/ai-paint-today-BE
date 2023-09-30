package tipitapi.drawmytoday.domain.diary.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import tipitapi.drawmytoday.common.BaseRepositoryTest;
import tipitapi.drawmytoday.common.config.QuerydslConfig;
import tipitapi.drawmytoday.domain.diary.domain.Diary;
import tipitapi.drawmytoday.domain.diary.domain.Image;
import tipitapi.drawmytoday.domain.user.domain.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import(QuerydslConfig.class)
class ImageRepositoryTest extends BaseRepositoryTest {

    @Autowired
    ImageRepository imageRepository;

    @Nested
    @DisplayName("findByIsSelectedTrueAndDiary 메소드 테스트")
    class findByIsSelectedTrueAndDiaryTest {

        @Nested
        @DisplayName("주어진 일기의 선택된 이미지가 존재할 경우")
        class if_selected_image_of_diary_exists {

            @Test
            @DisplayName("이미지를 반환한다.")
            void return_image() {
                Diary diary = createDiaryWithId(1L, createUser());
                Image image = createImage(1L, diary);

                Optional<Image> foundImage = imageRepository.findByIsSelectedTrueAndDiary(diary);

                assertThat(foundImage.isPresent()).isTrue();
                assertThat(foundImage.get().getImageId()).isEqualTo(image.getImageId());
            }
        }

        @Nested
        @DisplayName("주어진 일기의 이미지 중, 선택된 이미지가 존재하지 않을 경우")
        class if_selected_image_of_diary_not_exists {

            @Test
            @DisplayName("null을 반환한다.")
            void return_null() {
                Diary diary = createDiaryWithId(1L, createUser());
                createImage(1L, diary).setSelected(false);

                Optional<Image> foundImage = imageRepository.findByIsSelectedTrueAndDiary(diary);

                assertThat(foundImage).isEmpty();
            }
        }

        @Nested
        @DisplayName("주어진 일기가 존재하지 않는 경우")
        class if_diary_not_exists {

            @Test
            @DisplayName("null을 반환한다.")
            void return_null() {
                User user = createUser();
                Diary diary = createDiaryWithId(1L, user);
                Diary otherDiary = createDiaryWithId(2L, user);
                createImage(1L, diary);

                Optional<Image> foundImage = imageRepository.findByIsSelectedTrueAndDiary(
                    otherDiary);

                assertThat(foundImage).isEmpty();
            }
        }
    }

    @Nested
    @DisplayName("findAllByDiaryDiaryId 메소드 테스트")
    class FindAllByDiaryDiaryIdTest {

        @Nested
        @DisplayName("주어진 일기의 이미지가 존재할 경우")
        class if_images_of_diary_exists {

            @Test
            @DisplayName("이미지 목록을 반환한다.")
            void return_image_list() {
                Diary diary = createDiaryWithId(1L, createUser());
                Image image1 = createImage(1L, diary);
                Image image2 = createImage(2L, diary);

                assertThat(imageRepository.findAllByDiaryDiaryId(diary.getDiaryId()))
                    .containsExactlyInAnyOrder(image1, image2);
            }
        }

        @Nested
        @DisplayName("주어진 일기의 이미지가 존재하지 않을 경우")
        class if_images_of_diary_not_exists {

            @Test
            @DisplayName("빈 목록을 반환한다.")
            void return_empty_list() {
                Diary diary = createDiaryWithId(1L, createUser());

                assertThat(imageRepository.findAllByDiaryDiaryId(diary.getDiaryId()))
                    .isEmpty();
            }
        }
    }

    @Nested
    @DisplayName("findLatestByDiary 메서드 테스트")
    class FindLatestByDiary {

        @Test
        @DisplayName("주어진 일기의 최신순 이미지 목록을 반환한다.")
        void it_returns_latest_images_of_diary() {
            Diary diary = createDiaryWithId(1L, createUser());
            Image image1 = createImage(1L, diary);
            Image image2 = createImage(2L, diary);
            Image deletedImage = createImage(3L, diary);
            imageRepository.delete(deletedImage);

            List<Image> result = imageRepository.findLatestByDiary(diary.getDiaryId());

            assertThat(result.size()).isEqualTo(2);
            assertThat(result.get(0).getImageId()).isEqualTo(image2.getImageId());
            assertThat(result.get(1).getImageId()).isEqualTo(image1.getImageId());
        }
    }

    @Nested
    @DisplayName("findImage 메소드 테스트")
    class FindImageTest {

        @Nested
        @DisplayName("주어진 이미지가 존재할 경우")
        class if_image_exists {

            @Test
            @DisplayName("이미지를 반환한다.")
            void return_image() {
                Diary diary = createDiaryWithId(1L, createUser());
                Image image = createImage(1L, diary);

                Optional<Image> foundImage = imageRepository.findImage(image.getImageId());

                assertThat(foundImage.isPresent()).isTrue();
                assertThat(foundImage.get().getImageId()).isEqualTo(image.getImageId());
            }
        }

        @Nested
        @DisplayName("주어진 이미지가 존재하지 않을 경우")
        class if_image_not_exists {

            @Test
            @DisplayName("null을 반환한다.")
            void return_null() {
                assertThat(imageRepository.findImage(1L)).isEmpty();
            }
        }

        @Nested
        @DisplayName("주어진 이미지가 삭제되었을 경우")
        class if_image_deleted {

            @Test
            @DisplayName("null을 반환한다.")
            void return_null() {
                Long imageId = 1L;
                Diary diary = createDiaryWithId(1L, createUser());
                Image deletedImage = createImage(imageId, diary);
                imageRepository.delete(deletedImage);

                assertThat(imageRepository.findImage(imageId)).isEmpty();
            }
        }
    }

    @Nested
    @DisplayName("countImage 메소드 테스트")
    class CountImageTest {

        @Test
        @DisplayName("해당 일기의 이미지 개수를 반환한다.")
        void return_count_of_images() {
            Diary diary = createDiaryWithId(1L, createUser());
            createImage(1L, diary);
            createImage(2L, diary);
            imageRepository.delete(createImage(3L, diary));

            assertThat(imageRepository.countImage(diary.getDiaryId())).isEqualTo(2L);
        }

    }
}