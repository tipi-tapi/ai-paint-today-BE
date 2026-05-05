package tipitapi.drawmytoday.domain.oauth.domain;

import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tipitapi.drawmytoday.common.entity.BaseEntity;
import tipitapi.drawmytoday.domain.user.domain.User;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Auth extends BaseEntity {

    private Long authId;

    private User user;

    @NotNull
    private String refreshToken;

    private Auth(User user, String refreshToken) {
        this.user = user;
        this.refreshToken = refreshToken;
    }

    private Auth(Long authId, User user, String refreshToken, LocalDateTime createdAt) {
        super(createdAt);
        this.authId = authId;
        this.user = user;
        this.refreshToken = refreshToken;
    }

    public static Auth create(User user, String refreshToken) {
        return new Auth(user, refreshToken);
    }

    public static Auth restore(Long authId, User user, String refreshToken, LocalDateTime createdAt) {
        return new Auth(authId, user, refreshToken, createdAt);
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
