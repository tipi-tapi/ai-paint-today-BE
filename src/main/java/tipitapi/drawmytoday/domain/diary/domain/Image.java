package tipitapi.drawmytoday.domain.diary.domain;

import java.time.LocalDateTime;
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
import org.hibernate.annotations.SQLDelete;
import tipitapi.drawmytoday.common.entity.BaseEntity;

@SQLDelete(sql = "UPDATE image SET deleted_at = current_timestamp WHERE image_id = ?")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Image extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_id", nullable = false)
    private Diary diary;

    @NotNull
    @Column(nullable = false)
    private String imageUrl;

    @NotNull
    private boolean isSelected;

    private String review;

    private LocalDateTime deletedAt;

    private Image(Diary diary, String imageUrl, boolean isSelected) {
        this.diary = diary;
        diary.getImageList().add(this);
        this.imageUrl = imageUrl;
        this.isSelected = isSelected;
    }

    public static Image create(Diary diary, String imageUrl, boolean isSelected) {
        return new Image(diary, imageUrl, isSelected);
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public void reviewImage(String review) {
        this.review = review;
    }
}
