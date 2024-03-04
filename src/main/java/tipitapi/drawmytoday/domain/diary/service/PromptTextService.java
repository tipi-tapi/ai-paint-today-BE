package tipitapi.drawmytoday.domain.diary.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tipitapi.drawmytoday.domain.diary.domain.Prompt;
import tipitapi.drawmytoday.domain.diary.domain.PromptGeneratorResult;
import tipitapi.drawmytoday.domain.emotion.domain.Emotion;
import tipitapi.drawmytoday.domain.generator.domain.TextGeneratorContent;
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
    public Prompt createPrompt(Emotion emotion, String keyword) {
        if (!StringUtils.hasText(keyword)) {
            keyword = "portrait";
        }
        String finalPromptText = promptTextBuilder(
            emotion.getEmotionPrompt(),
            emotion.getColorPrompt(),
            defaultStyle,
            keyword);
        return Prompt.create(finalPromptText);
    }

    public Prompt createPromptUsingGpt(Emotion emotion, String diaryNote) {
        String promptText;
        List<? extends TextGeneratorContent> gptResult = null;
        if (!StringUtils.hasText(diaryNote)) {
            promptText = "portrait";
        } else {
            try {
                gptResult = gptService.generatePrompt(diaryNote);
                promptText = gptResult.get(gptResult.size() - 1).getContent();
            } catch (TextGeneratorException e) {
                promptText = diaryNote;
            }
        }

        String finalPromptText = promptTextBuilder(
            emotion.getEmotionPrompt(),
            emotion.getColorPrompt(),
            defaultStyle,
            promptText);
        PromptGeneratorResult result = PromptGeneratorResult.createGpt3Result(gptResult);
        return Prompt.create(result, finalPromptText);
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
