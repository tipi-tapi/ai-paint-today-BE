package tipitapi.drawmytoday.domain.generator.api.stability.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
        ObjectMapper objectMapper = new ObjectMapper();

        // JSON 객체 생성
        ObjectNode requestBody = objectMapper.createObjectNode();
        ObjectNode textPrompt1 = objectMapper.createObjectNode();
        textPrompt1.put("text", prompt);
        textPrompt1.put("weight", 1);

        ObjectNode textPrompt2 = objectMapper.createObjectNode();
        textPrompt2.put("text", negativePrompt);
        textPrompt2.put("weight", -1);

        requestBody.set("text_prompts",
            objectMapper.createArrayNode().add(textPrompt1).add(textPrompt2));
        requestBody.put("cfg_scale", 7);
        requestBody.put("height", 512);
        requestBody.put("width", 512);
        requestBody.put("samples", 1);
        requestBody.put("steps", 30);

        try {
            return new HttpEntity<>(objectMapper.writeValueAsString(requestBody), headers);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("해당 prompt를 파싱할 수 없습니다. prompt:" + prompt, e);
        }
    }

}
