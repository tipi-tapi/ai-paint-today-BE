package tipitapi.drawmytoday.domain.diary.domain;

import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tipitapi.drawmytoday.common.entity.BaseEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image extends BaseEntity {

    private Long imageId;

    @NotNull
    private Diary diary;

    @NotNull
    private Prompt prompt;

    @NotNull
    private String imageUrl;

    @NotNull
    private boolean isSelected;

    private String review;

    private LocalDateTime deletedAt;

    private Image(Diary diary, Prompt prompt, String imageUrl, boolean isSelected) {
        this.diary = diary;
        diary.getImageList().add(this);
        this.prompt = prompt;
        this.imageUrl = imageUrl;
        this.isSelected = isSelected;
    }

    private Image(Long imageId, Diary diary, Prompt prompt, String imageUrl, boolean isSelected,
        String review, LocalDateTime deletedAt, LocalDateTime createdAt) {
        super(createdAt);
        this.imageId = imageId;
        this.diary = diary;
        this.prompt = prompt;
        this.imageUrl = imageUrl;
        this.isSelected = isSelected;
        this.review = review;
        this.deletedAt = deletedAt;
    }

    public static Image create(Diary diary, Prompt prompt, String imageUrl, boolean isSelected) {
        return new Image(diary, prompt, imageUrl, isSelected);
    }

    public static Image restore(Long imageId, Diary diary, Prompt prompt, String imageUrl,
        boolean isSelected, String review, LocalDateTime deletedAt, LocalDateTime createdAt) {
        return new Image(imageId, diary, prompt, imageUrl, isSelected, review, deletedAt,
            createdAt);
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public void reviewImage(String review) {
        this.review = review;
    }
}
