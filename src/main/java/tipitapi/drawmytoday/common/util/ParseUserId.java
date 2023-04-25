package tipitapi.drawmytoday.common.util;

import java.util.Optional;
import org.springframework.security.core.Authentication;

public class ParseUserId {
  public static Long parseUserId(Authentication authentication) {
    if (Optional.ofNullable(authentication).isPresent()) {
      return Long.parseLong(authentication.getName());
    }
    return 1L;
  }

}
