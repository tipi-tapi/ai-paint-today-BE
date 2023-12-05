package tipitapi.drawmytoday.domain.diary.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tipitapi.drawmytoday.domain.emotion.domain.Emotion;

@Service
@Transactional(readOnly = true)
public class PromptTextService {

    private final String defaultStyle;

    public PromptTextService(
        @Value("${kakao.karlo.generate_image.style.default}") String defaultStyle) {
        this.defaultStyle = defaultStyle;
    }

    public String createPromptText(Emotion emotion, String keyword) {
        if (!StringUtils.hasText(keyword)) {
            keyword = "portrait";
        }
        return promptTextBuilder(
            emotion.getEmotionPrompt(),
            emotion.getColorPrompt(),
            defaultStyle,
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
