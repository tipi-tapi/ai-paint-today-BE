package tipitapi.drawmytoday.domain.generator.api.stability.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@Slf4j
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreateStabilityImageRequest {

    public static HttpEntity<String> getRequest(String prompt, String negativePrompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(MediaType.parseMediaTypes("image/png"));
        headers.setContentType(MediaType.APPLICATION_JSON);
        String requestBody = "{\n" +
            "    \"text_prompts\": [\n" +
            "      {\n" +
            "        \"text\": \"" + prompt + "\",\n" +
            "        \"weight\": 1\n" +
            "      },\n" +
            "      {\n" +
            "        \"text\": \"" + negativePrompt + "\",\n" +
            "        \"weight\": -1\n" +
            "      }\n" +
            "    ],\n" +
            "    \"cfg_scale\": 7,\n" +
            "    \"height\": 512,\n" +
            "    \"width\": 512,\n" +
            "    \"samples\": 1,\n" +
            "    \"steps\": 30\n" +
            "}";
        return new HttpEntity<>(requestBody, headers);
    }

}
