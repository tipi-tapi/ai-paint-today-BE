package tipitapi.drawmytoday.user.domain;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import tipitapi.drawmytoday.common.entity.BaseEntity;
import tipitapi.drawmytoday.common.entity.BaseEntityWithUpdate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntityWithUpdate {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long userId;

  private String email;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private SocialCode socialCode;

  private LocalDateTime lastDiaryDate;

  private User(SocialCode socialCode) {
    this.socialCode = socialCode;
  }

  private User(String email, SocialCode socialCode) {
    this.email = email;
    this.socialCode = socialCode;
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
}
