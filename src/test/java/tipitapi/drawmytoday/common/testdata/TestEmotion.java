package tipitapi.drawmytoday.common.testdata;

import tipitapi.drawmytoday.emotion.domain.Emotion;

public class TestEmotion {

    public static Emotion createEmotion() {
        return Emotion.create("HAPPY", "#12312", true, "example emotion prompt",
            "example color prompt");
    }
}
