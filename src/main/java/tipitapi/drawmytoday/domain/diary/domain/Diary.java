package tipitapi.drawmytoday.domain.diary.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tipitapi.drawmytoday.common.entity.BaseEntityWithUpdate;
import tipitapi.drawmytoday.domain.diary.exception.ImageNotFoundException;
import tipitapi.drawmytoday.domain.emotion.domain.Emotion;
import tipitapi.drawmytoday.domain.user.domain.User;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Diary extends BaseEntityWithUpdate {

    private String diaryId;

    @NotNull
    private User user;

    @NotNull
    private Emotion emotion;

    @NotNull
    private LocalDateTime diaryDate;

    private String notes;

    @NotNull
    private boolean isAi;

    private String title;

    private String weather;

    private List<Image> imageList;

    private LocalDateTime deletedAt;

    @NotNull
    private boolean isTest;

    @Builder
    private Diary(User user, Emotion emotion, LocalDateTime diaryDate, String notes, boolean isAi,
        String title, String weather, boolean isTest) {
        this.user = user;
        this.emotion = emotion;
        this.diaryDate = diaryDate;
        this.notes = notes;
        this.isAi = isAi;
        this.title = title;
        this.weather = weather;
        this.isTest = isTest;
        this.imageList = new ArrayList<>();
    }

    private Diary(String diaryId, User user, Emotion emotion, LocalDateTime diaryDate, String notes,
        boolean isAi, String title, String weather, List<Image> imageList,
        LocalDateTime deletedAt, boolean isTest, LocalDateTime createdAt,
        LocalDateTime updatedAt) {
        super(createdAt, updatedAt);
        this.diaryId = diaryId;
        this.user = user;
        this.emotion = emotion;
        this.diaryDate = diaryDate;
        this.notes = notes;
        this.isAi = isAi;
        this.title = title;
        this.weather = weather;
        this.imageList = imageList != null ? imageList : new ArrayList<>();
        this.deletedAt = deletedAt;
        this.isTest = isTest;
    }

    public static Diary of(User user, Emotion emotion, LocalDateTime diaryDateTime, String notes) {
        return Diary.builder()
            .user(user)
            .emotion(emotion)
            .diaryDate(diaryDateTime)
            .notes(notes)
            .isAi(true)
            .isTest(false)
            .build();
    }

    public static Diary ofTest(User user, Emotion emotion, LocalDateTime diaryDateTime,
        String notes) {
        return Diary.builder()
            .user(user)
            .emotion(emotion)
            .diaryDate(diaryDateTime)
            .notes(notes)
            .isAi(true)
            .isTest(true)
            .build();
    }

    public static Diary restore(String diaryId, User user, Emotion emotion, LocalDateTime diaryDate,
        String notes, boolean isAi, String title, String weather, List<Image> imageList,
        LocalDateTime deletedAt, boolean isTest, LocalDateTime createdAt,
        LocalDateTime updatedAt) {
        return new Diary(diaryId, user, emotion, diaryDate, notes, isAi, title, weather, imageList,
            deletedAt, isTest, createdAt, updatedAt);
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Image getSelectedImage() {
        return imageList.stream()
            .filter(Image::isSelected)
            .findFirst()
            .orElseThrow(ImageNotFoundException::new);
    }

}
