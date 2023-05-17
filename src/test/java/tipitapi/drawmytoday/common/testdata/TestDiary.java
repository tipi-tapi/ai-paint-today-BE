package tipitapi.drawmytoday.common.testdata;

import java.time.LocalDateTime;
import org.springframework.test.util.ReflectionTestUtils;
import tipitapi.drawmytoday.diary.domain.Diary;
import tipitapi.drawmytoday.emotion.domain.Emotion;
import tipitapi.drawmytoday.user.domain.User;

public class TestDiary {

    public static Diary createDiary(User user, Emotion emotion) {
        return Diary.builder().user(user).emotion(emotion)
            .diaryDate(LocalDateTime.now()).isAi(true).build();
    }

    public static Diary createDiaryWithId(Long diaryId, User user, Emotion emotion) {
        Diary diary = createDiary(user, emotion);
        ReflectionTestUtils.setField(diary, "diaryId", diaryId);
        return diary;
    }
}
