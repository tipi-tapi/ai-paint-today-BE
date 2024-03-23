package tipitapi.drawmytoday.domain.diary.domain;

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

    private PromptGeneratorResult(PromptGeneratorType promptGeneratorType,
        String promptGeneratorContent) {
        this.promptGeneratorType = promptGeneratorType;
        this.promptGeneratorContent = promptGeneratorContent;
    }

    public static PromptGeneratorResult createGpt3Result(String gptResult) {
        return new PromptGeneratorResult(PromptGeneratorType.GPT3, gptResult);
    }

    public static PromptGeneratorResult createNoUse() {
        return new PromptGeneratorResult(PromptGeneratorType.NONE, null);
    }

    public PromptGeneratorType getPromptGeneratorType() {
        return promptGeneratorType;
    }

    public String getPromptGeneratorContent() {
        return promptGeneratorContent;
    }
}
