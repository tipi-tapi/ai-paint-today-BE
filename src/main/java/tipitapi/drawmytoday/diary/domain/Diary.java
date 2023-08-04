package tipitapi.drawmytoday.diary.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import tipitapi.drawmytoday.common.entity.BaseEntityWithUpdate;
import tipitapi.drawmytoday.emotion.domain.Emotion;
import tipitapi.drawmytoday.user.domain.User;

@SQLDelete(sql = "UPDATE diary SET deleted_at = current_timestamp WHERE diary_id = ?")
@Where(clause = "deleted_at is null")
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

    @Column(length = 8013)
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

    private LocalDateTime deletedAt;

    @NotNull
    @Column(nullable = false)
    private boolean isTest;

    @Builder
    private Diary(User user, Emotion emotion, LocalDateTime diaryDate, String notes, boolean isAi,
        String title,
        String weather, ReviewType review, boolean isTest) {
        this.user = user;
        this.emotion = emotion;
        this.diaryDate = diaryDate;
        this.notes = notes;
        this.isAi = isAi;
        this.title = title;
        this.weather = weather;
        this.review = review;
        this.isTest = isTest;
        this.imageList = new ArrayList<>();
    }

    public static Diary of(User user, Emotion emotion, LocalDate diaryDate, String notes) {
        return Diary.builder()
            .user(user)
            .emotion(emotion)
            .diaryDate(diaryDate.atTime(LocalTime.now()))
            .notes(notes)
            .isAi(true)
            .isTest(false)
            .build();
    }

    public static Diary ofTest(User user, Emotion emotion, LocalDate diaryDate, String notes) {
        return Diary.builder()
            .user(user)
            .emotion(emotion)
            .diaryDate(diaryDate.atTime(LocalTime.now()))
            .notes(notes)
            .isAi(true)
            .isTest(true)
            .build();
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public ZonedDateTime getDiaryDateWithZone() {
        return this.diaryDate.atZone(ZoneOffset.UTC);
    }
}
