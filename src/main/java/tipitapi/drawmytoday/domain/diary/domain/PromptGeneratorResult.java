package tipitapi.drawmytoday.domain.diary.domain;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PromptGeneratorResult {

    @NotNull
    private PromptGeneratorType promptGeneratorType;
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

    public static PromptGeneratorResult restore(PromptGeneratorType promptGeneratorType,
        String promptGeneratorContent) {
        return new PromptGeneratorResult(promptGeneratorType, promptGeneratorContent);
    }

    public PromptGeneratorType getPromptGeneratorType() {
        return promptGeneratorType;
    }

    public String getPromptGeneratorContent() {
        return promptGeneratorContent;
    }
}
