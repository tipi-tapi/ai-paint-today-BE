package tipitapi.drawmytoday.emotion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tipitapi.drawmytoday.emotion.domain.Emotion;

@Getter
@Schema(description = "감정 등록 Request")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateEmotionRequest {

    @NotBlank
    @Schema(description = "감정 이름(한국어)")
    private String emotionName;

    @NotBlank
    @Schema(description = "감정 프롬프트 값")
    private String emotionPrompt;

    @NotBlank
    @Schema(description = "감정 색상 HEX 코드")
    private String colorHex;

    @NotBlank
    @Schema(description = "감정 색상 프롬프트 값")
    private String colorPrompt;

    public Emotion toEmotionEntity() {
        return Emotion.create(emotionName, colorHex, true, emotionPrompt, colorPrompt);
    }
}
