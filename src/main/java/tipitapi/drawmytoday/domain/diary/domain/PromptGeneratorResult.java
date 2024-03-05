package tipitapi.drawmytoday.domain.diary.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PromptGeneratorResult {

    @NotNull
    @Enumerated(EnumType.STRING)
    private PromptGeneratorType promptGeneratorType;
    @Type(type = "text")
    private String promptGeneratorContent;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private PromptGeneratorResult(PromptGeneratorType promptGeneratorType,
        String promptGeneratorContent) {
        this.promptGeneratorType = promptGeneratorType;
        this.promptGeneratorContent = promptGeneratorContent;
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

    public String getPromptGeneratorContent() {
        return promptGeneratorContent;
    }
}
