package tipitapi.drawmytoday.domain.diary.domain;

import javax.persistence.Column;
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

    @NotNull
    @Column(length = 1100)
    private String promptText;

    @NotNull
    private boolean isSuccess;

    private Prompt(String promptText, boolean isSuccess) {
        this.promptText = promptText;
        this.isSuccess = isSuccess;
    }

    public static Prompt create(String promptText, boolean isSuccess) {
        return new Prompt(promptText, isSuccess);
    }
}
