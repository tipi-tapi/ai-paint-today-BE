package tipitapi.drawmytoday.common.testdata;

import java.time.LocalDateTime;
import org.springframework.test.util.ReflectionTestUtils;
import tipitapi.drawmytoday.domain.diary.domain.Diary;
import tipitapi.drawmytoday.domain.emotion.domain.Emotion;
import tipitapi.drawmytoday.domain.user.domain.User;

public class TestDiary {

    public static Diary createDiary(User user, Emotion emotion) {
        return Diary.of(user, emotion, LocalDateTime.now(), null);
    }

    public static Diary createTestDiary(User user, Emotion emotion) {
        return Diary.ofTest(user, emotion, LocalDateTime.now(), null);
    }

    public static Diary createDiaryWithId(Long diaryId, User user, Emotion emotion) {
        Diary diary = createDiary(user, emotion);
        ReflectionTestUtils.setField(diary, "diaryId", diaryId);
        return diary;
    }

    public static Diary createDiaryWithDate(LocalDateTime diaryDate, User user, Emotion emotion) {
        Diary diary = createDiary(user, emotion);
        ReflectionTestUtils.setField(diary, "diaryDate", diaryDate);
        return diary;
    }

    public static Diary createDiaryWithIdAndCreatedAt(Long diaryId, LocalDateTime createdAt,
        User user,
        Emotion emotion) {
        Diary diary = createDiary(user, emotion);
        ReflectionTestUtils.setField(diary, "diaryId", diaryId);
        ReflectionTestUtils.setField(diary, "createdAt", createdAt);
        return diary;
    }

    public static Diary createDiaryWithCreatedAt(LocalDateTime createdAt, User user,
        Emotion emotion) {
        Diary diary = createDiary(user, emotion);
        ReflectionTestUtils.setField(diary, "createdAt", createdAt);
        return diary;
    }
}
