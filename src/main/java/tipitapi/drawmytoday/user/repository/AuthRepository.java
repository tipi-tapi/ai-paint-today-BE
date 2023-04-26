package tipitapi.drawmytoday.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tipitapi.drawmytoday.user.domain.Auth;

public interface AuthRepository extends JpaRepository<Auth, Long> {

}
