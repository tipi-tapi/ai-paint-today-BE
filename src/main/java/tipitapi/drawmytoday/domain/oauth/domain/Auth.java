package tipitapi.drawmytoday.domain.oauth.domain;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tipitapi.drawmytoday.common.entity.BaseEntity;
import tipitapi.drawmytoday.domain.user.domain.User;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Auth extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long authId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @Column(nullable = false)
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
