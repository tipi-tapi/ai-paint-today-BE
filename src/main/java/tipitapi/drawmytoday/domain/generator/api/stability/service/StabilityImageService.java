package tipitapi.drawmytoday.domain.generator.api.stability.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.domain.diary.domain.Prompt;
import tipitapi.drawmytoday.domain.diary.dto.CreateTestDiaryRequest;
import tipitapi.drawmytoday.domain.diary.service.PromptService;
import tipitapi.drawmytoday.domain.generator.exception.ImageGeneratorException;
import tipitapi.drawmytoday.domain.generator.service.ImageGeneratorService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
class StabilityImageService implements ImageGeneratorService {

    private final StabilityRequestService stabilityRequestService;
    private final PromptService promptService;
    private static final int MAX_PROMPT_LENGTH = 2048;

    @Override
    @Transactional(noRollbackFor = ImageGeneratorException.class)
    public byte[] generateImage(Prompt prompt) throws ImageGeneratorException {
        try {
            String promptText = substringPromptText(prompt.getPromptText());
            return stabilityRequestService.generateImage(promptText);
        } catch (ImageGeneratorException e) {
            promptService.savePrompt(prompt);
            throw e;
        }
    }

    @Override
    public List<byte[]> generateTestImage(CreateTestDiaryRequest request)
        throws ImageGeneratorException {
        return null;
    }

    private String substringPromptText(String promptText) {
        if (promptText.length() > MAX_PROMPT_LENGTH) {
            return promptText.substring(0, MAX_PROMPT_LENGTH);
        }
        return promptText;
    }
}
