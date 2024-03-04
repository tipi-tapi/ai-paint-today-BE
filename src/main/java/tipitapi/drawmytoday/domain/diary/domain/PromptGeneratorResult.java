package tipitapi.drawmytoday.domain.diary.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PromptGeneratorResult {

    @Enumerated(EnumType.STRING)
    private PromptGeneratorType promptGeneratorType;
    @Column(length = 3000)
    private String content;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private PromptGeneratorResult(PromptGeneratorType promptGeneratorType, String content) {
        this.promptGeneratorType = promptGeneratorType;
        this.content = content;
    }

    public static PromptGeneratorResult createGpt3Result(List<?> gptResult) {
        try {
            String content = objectMapper.writeValueAsString(gptResult);
            return new PromptGeneratorResult(PromptGeneratorType.GPT3, content);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("GPT 결과를 JSON으로 변환하는데 실패했습니다. response: "
                + gptResult.toString());
        }
    }

    public static PromptGeneratorResult createNoUse() {
        return new PromptGeneratorResult(PromptGeneratorType.NONE, "");
    }

    public PromptGeneratorType getPromptGeneratorType() {
        return promptGeneratorType;
    }

    public String getContent() {
        return content;
    }
}
