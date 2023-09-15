package tipitapi.drawmytoday.domain.diary.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tipitapi.drawmytoday.common.entity.BaseEntity;
import tipitapi.drawmytoday.domain.gallery.domain.Painting;

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

    @OneToOne(mappedBy = "image")
    private Painting painting;

    @NotNull
    @Column(nullable = false)
    private String imageUrl;

    @NotNull
    private boolean isSelected;

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
}
