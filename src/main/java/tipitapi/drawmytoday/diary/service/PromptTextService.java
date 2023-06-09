package tipitapi.drawmytoday.diary.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tipitapi.drawmytoday.emotion.domain.Emotion;

@Service
public class PromptTextService {

    public String createPromptText(Emotion emotion, String keyword) {
        return promptTextBuilder(
            emotion.getEmotionPrompt(),
            emotion.getColorPrompt(),
            "canvas-textured",
            "Oil Pastel",
            keyword);
    }

    private String promptTextBuilder(String... prompts) {
        StringBuilder sb = new StringBuilder();
        for (String prompt : prompts) {
            if (StringUtils.hasText(prompt)) {
                if (sb.length() != 0) {
                    sb.append(", ");
                }
                sb.append(prompt);
            }
        }
        if (sb.length() == 0) {
            return "";
        }
        return sb.toString();
    }

}
