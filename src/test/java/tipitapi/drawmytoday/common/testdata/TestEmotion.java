package tipitapi.drawmytoday.common.testdata;

import org.springframework.test.util.ReflectionTestUtils;
import tipitapi.drawmytoday.emotion.domain.Emotion;

public class TestEmotion {

    public static Emotion createEmotion() {
        return Emotion.create("행복", "#FF0000", true, "happy", "red");
    }

    public static Emotion createEmotionWithId(Long emotionId) {
        Emotion emotion = createEmotion();
        ReflectionTestUtils.setField(emotion, "emotionId", emotionId);
        return emotion;
    }

    public static Emotion createEmotionInActive() {
        return Emotion.create("SAD", "#45645", false, "example emotion prompt",
            "example color prompt");
    }
}
