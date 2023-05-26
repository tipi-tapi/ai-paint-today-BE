package tipitapi.drawmytoday.diary.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_id", nullable = true)
    private Diary diary;

    @NotNull
    @Column(length = 300)
    private String promptText;

    @NotNull
    private boolean isSuccess;

    private Prompt(Diary diary, String promptText, boolean isSuccess) {
        this.diary = diary;
        this.promptText = promptText;
        this.isSuccess = isSuccess;
    }

    public static Prompt create(Diary diary, String promptText, boolean isSuccess) {
        return new Prompt(diary, promptText, isSuccess);
    }
}
