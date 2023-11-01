package tipitapi.drawmytoday.domain.emotion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import tipitapi.drawmytoday.common.converter.Language;
import tipitapi.drawmytoday.domain.emotion.domain.Emotion;

@Getter
@Schema(description = "감정 목록 조회 Response. 활성화되어 일기 생성시 옵션으로 제공할 감정의 목록이다.")
@AllArgsConstructor
public class GetActiveEmotionsResponse {

    @Schema(description = "감정 ID", requiredMode = RequiredMode.REQUIRED)
    private final Long id;

    @Schema(description = "감정 이름", requiredMode = RequiredMode.REQUIRED)
    private final String name;

    @Schema(description = "감정 색깔 HEX", requiredMode = RequiredMode.REQUIRED)
    private final String color;

    @Schema(description = "감정 색깔 프롬프트값", requiredMode = RequiredMode.REQUIRED)
    private final String colorPrompt;

    public static GetActiveEmotionsResponse of(Long id, String name, String color,
        String colorPrompt) {
        return new GetActiveEmotionsResponse(id, name, color, colorPrompt);
    }

    public static List<GetActiveEmotionsResponse> buildWithEmotions(List<Emotion> emotions,
        Language language) {
        return emotions.stream()
            .map(e -> GetActiveEmotionsResponse.of(
                e.getEmotionId(), getEmotionName(language, e), e.getColor(), e.getColorPrompt()))
            .collect(
                Collectors.toList());
    }

    private static String getEmotionName(Language language, Emotion emotion) {
        if (Language.ko.equals(language)) {
            return emotion.getName();
        } else {
            return emotion.getEmotionPrompt();
        }
    }
}
