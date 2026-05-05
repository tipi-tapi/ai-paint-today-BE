package tipitapi.drawmytoday.domain.diary.domain;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tipitapi.drawmytoday.common.entity.BaseEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Prompt extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long promptId;

    @Embedded
    private PromptGeneratorResult promptGeneratorResult;

    @NotNull
    @Column(length = 3000)
    private String promptText;

    @NotNull
    private boolean isSuccess;

    private Prompt(PromptGeneratorResult promptGeneratorResult, String promptText) {
        this.promptGeneratorResult = promptGeneratorResult;
        this.promptText = promptText;
        this.isSuccess = false;
    }

    private Prompt(Long promptId, PromptGeneratorResult promptGeneratorResult, String promptText,
        boolean isSuccess, java.time.LocalDateTime createdAt) {
        super(createdAt);
        this.promptId = promptId;
        this.promptGeneratorResult = promptGeneratorResult;
        this.promptText = promptText;
        this.isSuccess = isSuccess;
    }

    public static Prompt create(PromptGeneratorResult promptGeneratorResult, String promptText) {
        return new Prompt(promptGeneratorResult, promptText);
    }

    public static Prompt create(String promptText) {
        return new Prompt(PromptGeneratorResult.createNoUse(), promptText);
    }

    public static Prompt restore(Long promptId, PromptGeneratorResult promptGeneratorResult,
        String promptText, boolean isSuccess, java.time.LocalDateTime createdAt) {
        return new Prompt(promptId, promptGeneratorResult, promptText, isSuccess, createdAt);
    }

    public void imageGeneratorSuccess() {
        this.isSuccess = true;
    }

    public void updatePromptGeneratorResult(PromptGeneratorResult promptGeneratorResult) {
        this.promptGeneratorResult = promptGeneratorResult;
    }
}
