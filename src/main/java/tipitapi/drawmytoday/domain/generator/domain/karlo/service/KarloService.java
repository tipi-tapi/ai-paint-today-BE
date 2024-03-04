package tipitapi.drawmytoday.domain.generator.domain.karlo.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.domain.diary.domain.Prompt;
import tipitapi.drawmytoday.domain.diary.dto.CreateTestDiaryRequest;
import tipitapi.drawmytoday.domain.diary.dto.CreateTestDiaryRequest.KarloParameter;
import tipitapi.drawmytoday.domain.diary.service.PromptService;
import tipitapi.drawmytoday.domain.generator.exception.ImageGeneratorException;
import tipitapi.drawmytoday.domain.generator.service.ImageGeneratorService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
class KarloService implements ImageGeneratorService {

    private final PromptService promptService;
    private final KarloRequestService karloRequestService;
    private static final int KARLO_MAX_PROMPT_LENGTH = 2048;

    @Override
    @Transactional(noRollbackFor = ImageGeneratorException.class)
    public byte[] generateImage(Prompt prompt) throws ImageGeneratorException {
        try {
            String promptText = substringPromptText(prompt.getPromptText());
            return karloRequestService.getImageAsUrl(promptText);
        } catch (ImageGeneratorException e) {
            promptService.savePrompt(prompt);
            throw e;
        }
    }

    @Override
    @Transactional(noRollbackFor = ImageGeneratorException.class)
    public List<byte[]> generateTestImage(CreateTestDiaryRequest request)
        throws ImageGeneratorException {
        KarloParameter param = request.getKarloParameter();
        try {
            return karloRequestService.getTestImageAsUrl(param);
        } catch (ImageGeneratorException e) {
            promptService.createPrompt(param.getPrompt(), false);
            throw e;
        }
    }

    private String substringPromptText(String promptText) {
        if (promptText.length() > KARLO_MAX_PROMPT_LENGTH) {
            return promptText.substring(0, KARLO_MAX_PROMPT_LENGTH);
        }
        return promptText;
    }
}
