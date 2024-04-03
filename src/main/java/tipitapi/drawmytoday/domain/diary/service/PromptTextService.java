package tipitapi.drawmytoday.domain.diary.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
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
    private static final int GPT_PROMPT_MAX_LENGTH = 150;

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

    public Prompt generatePromptUsingGpt(Emotion emotion, String diaryNote) {
        String promptText;
        PromptGeneratorResult result = null;
        try {
            List<? extends TextGeneratorContent> gptResult = gptService.generatePrompt(diaryNote,
                GPT_PROMPT_MAX_LENGTH);
            String parsingGptResult = objectMapper.writeValueAsString(gptResult);
            String gptContent = gptResult.get(gptResult.size() - 1).getContent();
            promptText = clampContent(gptContent);
            result = PromptGeneratorResult.createGpt3Result(parsingGptResult);
        } catch (TextGeneratorException e) {
            promptText = diaryNote;
            result = PromptGeneratorResult.createNoUse();
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("GPT 결과를 JSON으로 변환하는데 실패했습니다.", e);
        }

        String finalPromptText = promptTextBuilder(
            emotion.getEmotionPrompt(),
            emotion.getColorPrompt(),
            defaultStyle,
            promptText);
        return Prompt.create(result, finalPromptText);
    }

    public Prompt regeneratePromptUsingGpt(Emotion emotion, String diaryNote, Prompt prompt) {
        String promptText;
        PromptGeneratorResult result = null;
        try {
            List<? extends TextGeneratorContent> gptResult = gptService.regeneratePrompt(
                diaryNote, prompt);
            String parsingGptResult = objectMapper.writeValueAsString(gptResult);
            String gptContent = gptResult.get(gptResult.size() - 1).getContent();
            promptText = clampContent(gptContent);
            result = PromptGeneratorResult.createGpt3Result(parsingGptResult);
        } catch (TextGeneratorException e) {
            promptText = diaryNote;
            result = PromptGeneratorResult.createNoUse();
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("GPT 결과를 JSON으로 변환하는데 실패했습니다.", e);
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

    private String clampContent(String content) {
        if (content.length() > GPT_PROMPT_MAX_LENGTH) {
            content = content.substring(0, GPT_PROMPT_MAX_LENGTH);
        }
        String[] contents = content.split("\\.");
        if (contents.length == 1) {
            return content + ".";
        } else {
            return Arrays.stream(contents)
                .filter(StringUtils::hasText)
                .map(s -> s + ".")
                .reduce((s1, s2) -> s1 + " " + s2)
                .orElseThrow(() -> new IllegalArgumentException("GPT 결과를 clamping하는데 실패했습니다."));
        }
    }


}
