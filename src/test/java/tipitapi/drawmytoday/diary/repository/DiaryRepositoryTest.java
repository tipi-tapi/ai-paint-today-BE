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
import tipitapi.drawmytoday.common.BaseRepositoryTest;
import tipitapi.drawmytoday.diary.domain.Diary;
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
}