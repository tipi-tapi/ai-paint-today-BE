package tipitapi.drawmytoday.common.testdata;

import org.springframework.test.util.ReflectionTestUtils;
import tipitapi.drawmytoday.domain.diary.domain.Prompt;

public class TestPrompt {

    public static Prompt createPromptWithId(Long promptId, String promptText) {
        Prompt prompt = Prompt.create(promptText, true);
        ReflectionTestUtils.setField(prompt, "promptId", promptId);
        return prompt;
    }
}
