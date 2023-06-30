package tipitapi.drawmytoday.user.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import tipitapi.drawmytoday.common.BaseRepositoryTest;
import tipitapi.drawmytoday.common.testdata.TestUser;
import tipitapi.drawmytoday.user.domain.SocialCode;
import tipitapi.drawmytoday.user.domain.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UserRepositoryTest extends BaseRepositoryTest {

    @Nested
    @DisplayName("findByUserId 메소드 테스트")
    class FindByUserIdTest {

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

        @Nested
        @DisplayName("삭제된 유저가 존재할 경우")
        class If_deleted_user_exists {

            @Test
            @DisplayName("null을 반환한다.")
            void return_null() {
                User user = TestUser.createUser();
                user.deleteUser();
                userRepository.save(user);

                Optional<User> findUser = userRepository.findByUserId(user.getUserId());

                assertThat(findUser).isEmpty();
            }
        }

        @Nested
        @DisplayName("유저가 존재하지 않는 경우")
        class If_user_not_exist {

            @Test
            @DisplayName("null을 반환한다.")
            void return_null() {
                Long notExistUserId = 1L;

                Optional<User> findUser = userRepository.findByUserId(notExistUserId);

                assertThat(findUser).isEmpty();
            }
        }
    }

    @Nested
    @DisplayName("findAllByEmail 메서드 테스트")
    class FindAllByEmailTest {

        private final String email = "example@email.com";
        private final String otherEmail = "aaaa@email.com";

        @Nested
        @DisplayName("이메일에 해당하는 유저가 2명일 경우")
        class If_two_user_exists {

            @Test
            @DisplayName("두 유저를 반환한다.")
            void return_two_user_list() {
                User googleUser = userRepository.save(
                    User.createWithEmail(email, SocialCode.GOOGLE));
                User appleUser = userRepository.save(User.createWithEmail(email, SocialCode.APPLE));
                User otherUser = userRepository.save(
                    User.createWithEmail(otherEmail, SocialCode.GOOGLE));

                List<User> findUserList = userRepository.findAllByEmail(email);

                assertThat(findUserList).containsExactlyInAnyOrder(googleUser, appleUser);
                assertThat(findUserList).hasSize(2);
            }
        }

        @Nested
        @DisplayName("이메일에 해당하는 유저가 한 명일 경우")
        class If_one_user_exists {

            @Test
            @DisplayName("유저를 반환한다.")
            void return_one_user_list() {
                User googleUser = User.createWithEmail(email, SocialCode.GOOGLE);
                User otherUser = User.createWithEmail(otherEmail, SocialCode.GOOGLE);
                userRepository.save(googleUser);
                userRepository.save(otherUser);

                List<User> findUserList = userRepository.findAllByEmail(email);

                assertThat(findUserList).containsExactly(googleUser);
                assertThat(findUserList).hasSize(1);
            }
        }

        @Nested
        @DisplayName("이메일에 해당하는 유저가 없을 경우")
        class If_user_not_exists {

            @Test
            @DisplayName("빈 리스트를 반환한다.")
            void return_empty_list() {
                User otherUser = User.createWithEmail(otherEmail, SocialCode.GOOGLE);
                userRepository.save(otherUser);

                List<User> findUserList = userRepository.findAllByEmail(email);

                assertThat(findUserList).isEmpty();
            }
        }
    }

}
