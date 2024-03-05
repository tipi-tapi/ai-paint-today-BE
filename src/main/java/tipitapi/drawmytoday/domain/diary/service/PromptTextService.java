package tipitapi.drawmytoday.domain.diary.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper;

    public PromptTextService(
        @Value("${kakao.karlo.generate_image.style.default}") String defaultStyle,
        TextGeneratorService gptService,
        ObjectMapper objectMapper) {
        this.defaultStyle = defaultStyle;
        this.gptService = gptService;
        this.objectMapper = objectMapper;
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
        PromptGeneratorResult result = null;
        if (!StringUtils.hasText(diaryNote)) {
            promptText = "portrait";
        } else {
            try {
                List<? extends TextGeneratorContent> gptResult =
                    gptService.generatePrompt(diaryNote);
                String content = objectMapper.writeValueAsString(gptResult);
                promptText = gptResult.get(gptResult.size() - 1).getContent();
                result = PromptGeneratorResult.createGpt3Result(content);
            } catch (TextGeneratorException e) {
                promptText = diaryNote;
                result = PromptGeneratorResult.createNoUse();
            } catch (JsonProcessingException e) {
                throw new IllegalArgumentException("GPT 결과를 JSON으로 변환하는데 실패했습니다.", e);
            }
        }

        String finalPromptText = promptTextBuilder(
            emotion.getEmotionPrompt(),
            emotion.getColorPrompt(),
            defaultStyle,
            promptText);
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
