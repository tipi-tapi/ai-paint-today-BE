package tipitapi.drawmytoday.user.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import tipitapi.drawmytoday.user.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserIdAndDeletedAtIsNull(Long userId);

    Optional<User> findByEmail(String email);
}
