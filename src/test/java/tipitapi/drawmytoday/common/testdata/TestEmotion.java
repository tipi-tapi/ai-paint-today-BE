package tipitapi.drawmytoday.common.testdata;

import tipitapi.drawmytoday.domain.emotion.domain.Emotion;

public class TestEmotion {

    public static Emotion createEmotion() {
        return Emotion.builder()
            .name("행복")
            .color("#FF0000")
            .isActive(true)
            .emotionPrompt("happy")
            .colorPrompt("red")
            .build();
    }

    public static Emotion createEmotionWithId(Long emotionId) {
        return Emotion.restore(emotionId, "행복", "#FF0000", true, "happy", "red", null);
    }

    public static Emotion createEmotionInActive() {
        return Emotion.builder()
            .name("슬픔")
            .color("#0000FF")
            .isActive(false)
            .emotionPrompt("sad")
            .colorPrompt("blue")
            .build();
    }
}
