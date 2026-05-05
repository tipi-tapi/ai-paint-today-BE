package tipitapi.drawmytoday.domain.user.repository;

import tipitapi.drawmytoday.domain.user.domain.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findByUserId(Long userId);
    List<User> findAllByEmail(String email);
}
