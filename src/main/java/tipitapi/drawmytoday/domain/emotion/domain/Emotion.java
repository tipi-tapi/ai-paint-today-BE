package tipitapi.drawmytoday.domain.emotion.domain;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tipitapi.drawmytoday.common.entity.BaseEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Emotion extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long emotionId;

    @NotNull
    @Column(nullable = false, length = 30)
    private String name;

    @NotNull
    @Column(nullable = false, length = 30)
    private String color;

    @NotNull
    @Column(nullable = false)
    private boolean isActive;

    @NotNull
    @Column(nullable = false, length = 30)
    private String emotionPrompt;

    @NotNull
    @Column(nullable = false, length = 200)
    private String colorPrompt;

    @Builder
    private Emotion(String name, String color, boolean isActive, String emotionPrompt,
        String colorPrompt) {
        this.name = name;
        this.color = color;
        this.isActive = isActive;
        this.emotionPrompt = emotionPrompt;
        this.colorPrompt = colorPrompt;
    }

    private Emotion(Long emotionId, String name, String color, boolean isActive,
        String emotionPrompt, String colorPrompt, LocalDateTime createdAt) {
        super(createdAt);
        this.emotionId = emotionId;
        this.name = name;
        this.color = color;
        this.isActive = isActive;
        this.emotionPrompt = emotionPrompt;
        this.colorPrompt = colorPrompt;
    }

    public static Emotion restore(Long emotionId, String name, String color,
        boolean isActive, String emotionPrompt, String colorPrompt, LocalDateTime createdAt) {
        return new Emotion(emotionId, name, color, isActive, emotionPrompt, colorPrompt, createdAt);
    }
}
