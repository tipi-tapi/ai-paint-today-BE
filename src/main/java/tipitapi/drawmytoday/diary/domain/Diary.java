package tipitapi.drawmytoday.diary.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tipitapi.drawmytoday.common.entity.BaseEntityWithUpdate;
import tipitapi.drawmytoday.emotion.domain.Emotion;
import tipitapi.drawmytoday.user.domain.User;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Diary extends BaseEntityWithUpdate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long diaryId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emotion_id", nullable = false)
    private Emotion emotion;

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

    @OneToMany(mappedBy = "diary")
    private List<Image> imageList;

    @Builder
    public Diary(User user, Emotion emotion, LocalDateTime diaryDate, String notes, boolean isAi,
        String title,
        String weather, ReviewType review) {
        this.user = user;
        this.emotion = emotion;
        this.diaryDate = diaryDate;
        this.notes = notes;
        this.isAi = isAi;
        this.title = title;
        this.weather = weather;
        this.review = review;
        this.imageList = new ArrayList<>();
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
