package tipitapi.drawmytoday.diary.repository;

import static org.assertj.core.api.Assertions.assertThat;

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
import tipitapi.drawmytoday.diary.domain.Diary;
import tipitapi.drawmytoday.diary.domain.Image;
import tipitapi.drawmytoday.user.domain.User;

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
}