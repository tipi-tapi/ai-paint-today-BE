package tipitapi.drawmytoday.domain.oauth.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import tipitapi.drawmytoday.domain.oauth.domain.Auth;
import tipitapi.drawmytoday.domain.user.domain.User;

public interface AuthRepository extends JpaRepository<Auth, Long> {

    Optional<Auth> findByUser(User user);
}
