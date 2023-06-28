package tipitapi.drawmytoday.diary.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import tipitapi.drawmytoday.common.BaseRepositoryTest;
import tipitapi.drawmytoday.user.domain.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UserRepositoryTest extends BaseRepositoryTest {

    @Nested
    @DisplayName("findByUserId 메소드 테스트")
    class findByUserIdTest {

        @Nested
        @DisplayName("유저가 존재하는 경우")
        class If_user_exists {

            @Test
            @DisplayName("유저를 반환한다.")
            void return_user() {
                User user = createUser();

                Optional<User> findUser = userRepository.findByUserId(user.getUserId());

                assertThat(findUser.isPresent()).isTrue();
                assertThat(findUser.get().getUserId()).isEqualTo(user.getUserId());
            }
        }
    }

}
