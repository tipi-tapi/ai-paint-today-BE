package tipitapi.drawmytoday.domain.emotion.domain;

import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tipitapi.drawmytoday.common.entity.BaseEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Emotion extends BaseEntity {

    private String emotionId;

    @NotNull
    private String name;

    @NotNull
    private String color;

    @NotNull
    private boolean isActive;

    @NotNull
    private String emotionPrompt;

    @NotNull
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

    private Emotion(String emotionId, String name, String color, boolean isActive,
        String emotionPrompt, String colorPrompt, LocalDateTime createdAt) {
        super(createdAt);
        this.emotionId = emotionId;
        this.name = name;
        this.color = color;
        this.isActive = isActive;
        this.emotionPrompt = emotionPrompt;
        this.colorPrompt = colorPrompt;
    }

    public static Emotion restore(String emotionId, String name, String color,
        boolean isActive, String emotionPrompt, String colorPrompt, LocalDateTime createdAt) {
        return new Emotion(emotionId, name, color, isActive, emotionPrompt, colorPrompt, createdAt);
    }
}
