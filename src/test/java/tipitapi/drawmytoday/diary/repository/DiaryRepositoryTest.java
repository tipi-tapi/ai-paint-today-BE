package tipitapi.drawmytoday.diary.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import tipitapi.drawmytoday.diary.domain.Diary;
import tipitapi.drawmytoday.user.repository.UserRepository;

@DataJpaTest
@Import({DiaryRepository.class, Diary.class})
@AutoConfigureTestDatabase(replace = Replace.NONE)
class DiaryRepositoryTest {

    @Autowired
    DiaryRepository diaryRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("주어진 id와 user로 일기를 찾는다.")
    void findByIdAndUser() {

    }

    @Test
    @DisplayName("주어진 id의 일기가 없으면 null을 반환한다.")
    void findByIdAndUser_not_exist() {
    }

    @Test
    @DisplayName("주어진 user의 일기가 아니면 null을 반환한다.")
    void findByIdAndUser_not_owned_user() {
    }
}