package tipitapi.drawmytoday.domain.generator.domain.karlo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Request Body 정보:
 * https://developers.kakao.com/docs/latest/ko/karlo/rest-api#text-to-image-request-body
 */

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreateKarloImageRequest {

    private String prompt;
    private String negativePrompt;
    private String imageFormat;
    private Integer samples;
    private String returnType;

    public static CreateKarloImageRequest withUrl(String prompt) {
        return new CreateKarloImageRequest(prompt, null, "webp", 1, "url");
    }
}
