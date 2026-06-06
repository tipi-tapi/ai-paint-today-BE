package tipitapi.drawmytoday.domain.user.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tipitapi.drawmytoday.common.entity.BaseEntityWithUpdate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntityWithUpdate {

    private String userId;

    @NotNull
    private String email;

    @NotNull
    private SocialCode socialCode;

    private LocalDateTime lastDiaryDate;

    private UserRole userRole;

    private LocalDateTime deletedAt;

    private String refreshToken;

    private String appleIdToken;

    private String googleSub;

    @Builder
    private User(String email, SocialCode socialCode) {
        this.email = email;
        this.socialCode = socialCode;
        this.userRole = UserRole.USER;
    }

    private User(String userId, String email, SocialCode socialCode, UserRole userRole,
                 LocalDateTime lastDiaryDate, LocalDateTime deletedAt,
                 LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(createdAt, updatedAt);
        this.userId = userId;
        this.email = email;
        this.socialCode = socialCode;
        this.userRole = userRole;
        this.lastDiaryDate = lastDiaryDate;
        this.deletedAt = deletedAt;
    }

    public static User restore(String userId, String email, SocialCode socialCode,
                                UserRole userRole, LocalDateTime lastDiaryDate,
                                LocalDateTime deletedAt, LocalDateTime createdAt,
                                LocalDateTime updatedAt) {
        return new User(userId, email, socialCode, userRole, lastDiaryDate, deletedAt, createdAt, updatedAt);
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setLastDiaryDate(LocalDateTime date) {
        this.lastDiaryDate = date;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void setAppleIdToken(String appleIdToken) {
        this.appleIdToken = appleIdToken;
    }

    public void setGoogleSub(String googleSub) {
        this.googleSub = googleSub;
    }

    public void deleteUser() {
        this.deletedAt = LocalDateTime.now();
    }

    public boolean checkDrawLimit() {
        return this.getLastDiaryDate() == null
            || !this.getLastDiaryDate().toLocalDate().equals(LocalDate.now());
    }

    public boolean isAdmin() {
        return this.userRole == UserRole.ADMIN;
    }
}
