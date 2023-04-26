package tipitapi.drawmytoday.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tipitapi.drawmytoday.user.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
