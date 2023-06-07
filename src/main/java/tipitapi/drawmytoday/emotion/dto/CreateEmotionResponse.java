package tipitapi.drawmytoday.emotion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import tipitapi.drawmytoday.emotion.domain.Emotion;

@Getter
@Schema(description = "감정 생성 Response")
@AllArgsConstructor
public class CreateEmotionResponse {

    @Schema(description = "추가된 감정 ID", requiredMode = RequiredMode.REQUIRED)
    private final Long id;

    @Schema(description = "추가된 감정 이름", requiredMode = RequiredMode.REQUIRED)
    private final String emotionName;

    public static CreateEmotionResponse of(Emotion emotion) {
        return new CreateEmotionResponse(emotion.getEmotionId(), emotion.getName());
    }
}
