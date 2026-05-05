package tipitapi.drawmytoday.domain.oauth.repository;

import java.util.Optional;
import tipitapi.drawmytoday.domain.oauth.domain.Auth;
import tipitapi.drawmytoday.domain.user.domain.User;

public interface AuthRepository {

    Auth save(Auth auth);
    Optional<Auth> findByUser(User user);
    Optional<Auth> findByRefreshToken(String refreshToken);
}
