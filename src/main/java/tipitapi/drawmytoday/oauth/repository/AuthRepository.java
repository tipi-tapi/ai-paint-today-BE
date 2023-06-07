package tipitapi.drawmytoday.oauth.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import tipitapi.drawmytoday.oauth.domain.Auth;
import tipitapi.drawmytoday.user.domain.User;

public interface AuthRepository extends JpaRepository<Auth, Long> {

    Optional<Auth> findByUser(User user);
}
