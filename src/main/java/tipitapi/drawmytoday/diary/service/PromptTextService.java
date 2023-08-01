package tipitapi.drawmytoday.diary.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tipitapi.drawmytoday.emotion.domain.Emotion;

@Service
@Transactional(readOnly = true)
public class PromptTextService {

    public String createPromptText(Emotion emotion, String keyword) {
        if (!StringUtils.hasText(keyword)) {
            keyword = "emotions";
        }
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
        if (sb.length() > 1000) {
            return sb.substring(0, 1000);
        }
        return sb.toString();
    }
}
