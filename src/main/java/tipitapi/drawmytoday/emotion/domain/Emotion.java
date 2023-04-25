package tipitapi.drawmytoday.emotion.domain;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import tipitapi.drawmytoday.common.entity.BaseEntity;
import tipitapi.drawmytoday.diary.domain.ReviewType;
import tipitapi.drawmytoday.user.domain.User;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Emotion extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long emotionId;

  @NotNull
  @Column(nullable = false,length = 30)
  private String name;

  @NotNull
  @Column(nullable = false,length = 30)
  private String color;

  @NotNull
  @Column(nullable = false)
  private boolean isActive;

  @NotNull
  @Column(nullable = false, length = 30)
  private String emotionPrompt;

  @NotNull
  @Column(nullable = false, length = 30)
  private String colorPrompt;

  private Emotion(String name, String color, boolean isActive, String emotionPrompt,
      String colorPrompt) {
    this.name = name;
    this.color = color;
    this.isActive = isActive;
    this.emotionPrompt = emotionPrompt;
    this.colorPrompt = colorPrompt;
  }

  public static Emotion create(String name, String color, boolean isActive, String emotionPrompt, String colorPrompt) {
    return new Emotion(name, color, isActive, emotionPrompt, colorPrompt);
  }
}
