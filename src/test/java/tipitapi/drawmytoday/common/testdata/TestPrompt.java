package tipitapi.drawmytoday.common.testdata;

import tipitapi.drawmytoday.domain.diary.domain.Prompt;

public class TestPrompt {

    public static Prompt createPromptWithId(Long promptId, String promptText) {
        return Prompt.restore(promptId, null, promptText, true, null);
    }
}
