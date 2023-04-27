package tipitapi.drawmytoday.diary.domain;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import tipitapi.drawmytoday.common.entity.BaseEntityWithUpdate;
import tipitapi.drawmytoday.user.domain.User;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@SQLDelete(sql = "UPDATE diary SET deleted = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted_at = null")
public class Diary extends BaseEntityWithUpdate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long diaryId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime diaryDate;

    @Column(length = 6010)
    private String notes;

    @NotNull
    @Column(nullable = false)
    private boolean isAi;

    @Column(length = 42)
    private String title;

    @Column(length = 32)
    private String weather;

    @Enumerated(EnumType.STRING)
    private ReviewType review;

    private LocalDateTime deletedAt;

    @Builder
    public Diary(User user, LocalDateTime diaryDate, String notes, boolean isAi, String title,
        String weather, ReviewType review) {
        this.user = user;
        this.diaryDate = diaryDate;
        this.notes = notes;
        this.isAi = isAi;
        this.title = title;
        this.weather = weather;
        this.review = review;
    }
}
