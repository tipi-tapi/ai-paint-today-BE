package tipitapi.drawmytoday.common.security.jwt;

import lombok.Builder;
import lombok.Getter;
import tipitapi.drawmytoday.user.domain.UserRole;

@Getter
public class JwtTokenInfo {

    private final Long userId;
    private final UserRole userRole;

    @Builder
    public JwtTokenInfo(Long userId, UserRole userRole) {
        this.userId = userId;
        this.userRole = userRole;
    }
}
