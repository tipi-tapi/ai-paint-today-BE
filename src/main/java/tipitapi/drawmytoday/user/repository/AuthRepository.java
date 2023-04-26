package tipitapi.drawmytoday.user.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import tipitapi.drawmytoday.user.domain.Auth;
import tipitapi.drawmytoday.user.domain.User;

public interface AuthRepository extends JpaRepository<Auth, Long> {

    Optional<Auth> findByUser(User user);
}
