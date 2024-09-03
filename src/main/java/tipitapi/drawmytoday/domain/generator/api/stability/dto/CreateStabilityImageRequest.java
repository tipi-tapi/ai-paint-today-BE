package tipitapi.drawmytoday.domain.generator.api.stability.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreateStabilityImageRequest {

    public static HttpEntity<MultiValueMap<String, Object>> getRequest(String prompt,
        String negativePrompt) {
        MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("output_format", "webp");
        requestBody.add("prompt", prompt);
        requestBody.add("negative_prompt", negativePrompt);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, "image/*");
        headers.set(HttpHeaders.CONTENT_TYPE, "multipart/form-data;");
        return new HttpEntity<>(requestBody, headers);
    }

}
