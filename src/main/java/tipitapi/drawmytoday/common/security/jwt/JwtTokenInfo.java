package tipitapi.drawmytoday.common.security.jwt;

import lombok.Builder;
import lombok.Getter;
import tipitapi.drawmytoday.domain.user.domain.UserRole;

@Getter
public class JwtTokenInfo {

    private final String userId;
    private final UserRole userRole;

    @Builder
    public JwtTokenInfo(String userId, UserRole userRole) {
        this.userId = userId;
        this.userRole = userRole;
    }
}
