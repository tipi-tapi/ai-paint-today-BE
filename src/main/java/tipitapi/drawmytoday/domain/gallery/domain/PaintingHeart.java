package tipitapi.drawmytoday.domain.gallery.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import tipitapi.drawmytoday.common.entity.BaseEntity;
import tipitapi.drawmytoday.domain.user.domain.User;

@Table(name = "painting_heart")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaintingHeart extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paintingHeartId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "painting_id", nullable = false)
    private Painting painting;

    @Builder(access = AccessLevel.PRIVATE)
    private PaintingHeart(User user, Painting painting) {
        this.user = user;
        this.painting = painting;
    }

    public static PaintingHeart createPaintingHeart(User user, Painting painting) {
        return PaintingHeart.builder()
            .user(user)
            .painting(painting)
            .build();
    }
}
