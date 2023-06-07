package tipitapi.drawmytoday.common.testdata;

import org.springframework.test.util.ReflectionTestUtils;
import tipitapi.drawmytoday.emotion.domain.Emotion;

public class TestEmotion {

    public static Emotion createEmotion() {
        return Emotion.create("HAPPY", "#12312", true, "example emotion prompt",
            "example color prompt");
    }

    public static Emotion createEmotionWithId(Long emotionId) {
        Emotion emotion = createEmotion();
        ReflectionTestUtils.setField(emotion, "emotionId", emotionId);
        return emotion;
    }
}
