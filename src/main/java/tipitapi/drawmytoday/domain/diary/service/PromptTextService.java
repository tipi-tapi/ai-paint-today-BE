package tipitapi.drawmytoday.domain.diary.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tipitapi.drawmytoday.domain.emotion.domain.Emotion;
import tipitapi.drawmytoday.domain.generator.exception.TextGeneratorException;
import tipitapi.drawmytoday.domain.generator.service.TextGeneratorService;

@Service
@Transactional(readOnly = true)
public class PromptTextService {

    private final String defaultStyle;
    private final TextGeneratorService gptService;

    public PromptTextService(
        @Value("${kakao.karlo.generate_image.style.default}") String defaultStyle,
        TextGeneratorService gptService) {
        this.defaultStyle = defaultStyle;
        this.gptService = gptService;
    }

    @Deprecated
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

    public String createPromptTextWithGpt(Emotion emotion, String diaryNote) {
        String prompt = null;
        if (!StringUtils.hasText(diaryNote)) {
            prompt = "portrait";
        } else {
            try {
                prompt = gptService.generatePrompt(diaryNote);
            } catch (TextGeneratorException e) {
                prompt = diaryNote;
            }
        }
        return promptTextBuilder(
            emotion.getEmotionPrompt(),
            emotion.getColorPrompt(),
            defaultStyle,
            prompt);
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
