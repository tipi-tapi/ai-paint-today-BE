package tipitapi.drawmytoday.domain.user.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;
import tipitapi.drawmytoday.common.entity.BaseEntityWithUpdate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "deleted_at is null")
public class User extends BaseEntityWithUpdate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @NotNull
    @Column(nullable = false)
    private String email;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SocialCode socialCode;

    private LocalDateTime lastDiaryDate;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    private LocalDateTime deletedAt;

    private User(SocialCode socialCode) {
        this.socialCode = socialCode;
    }

    @Builder
    private User(String email, SocialCode socialCode) {
        this.email = email;
        this.socialCode = socialCode;
        this.userRole = UserRole.USER;
    }

    public static User create(SocialCode socialCode) {
        return new User(socialCode);
    }

    public static User createWithEmail(String email, SocialCode socialCode) {
        return new User(email, socialCode);
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setLastDiaryDate(LocalDateTime date) {
        this.lastDiaryDate = date;
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
