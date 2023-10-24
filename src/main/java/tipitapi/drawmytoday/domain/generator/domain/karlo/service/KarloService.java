package tipitapi.drawmytoday.domain.generator.domain.karlo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.domain.diary.domain.Prompt;
import tipitapi.drawmytoday.domain.diary.dto.CreateTestDiaryRequest;
import tipitapi.drawmytoday.domain.diary.dto.CreateTestDiaryRequest.KarloParameter;
import tipitapi.drawmytoday.domain.diary.service.PromptService;
import tipitapi.drawmytoday.domain.diary.service.PromptTextService;
import tipitapi.drawmytoday.domain.emotion.domain.Emotion;
import tipitapi.drawmytoday.domain.generator.dto.GeneratedImageAndPrompt;
import tipitapi.drawmytoday.domain.generator.exception.ImageGeneratorException;
import tipitapi.drawmytoday.domain.generator.service.ImageGeneratorService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
class KarloService implements ImageGeneratorService {

    private final PromptTextService promptTextService;
    private final PromptService promptService;
    private final KarloRequestService karloRequestService;

    @Override
    @Transactional(noRollbackFor = ImageGeneratorException.class)
    public GeneratedImageAndPrompt generateImage(Emotion emotion, String keyword)
        throws ImageGeneratorException {
        String prompt = promptTextService.createPromptText(emotion, keyword);
        try {
            byte[] image = karloRequestService.getImageAsUrl(prompt);
            return new GeneratedImageAndPrompt(prompt, image);
        } catch (ImageGeneratorException e) {
            promptService.createPrompt(prompt, false);
            throw e;
        }
    }

    @Override
    @Transactional(noRollbackFor = ImageGeneratorException.class)
    public GeneratedImageAndPrompt generateImage(Prompt prompt) throws ImageGeneratorException {
        try {
            byte[] image = karloRequestService.getImageAsUrl(prompt.getPromptText());
            return new GeneratedImageAndPrompt(prompt.getPromptText(), image);
        } catch (ImageGeneratorException e) {
            promptService.createPrompt(prompt.getPromptText(), false);
            throw e;
        }
    }

    @Override
    @Transactional(noRollbackFor = ImageGeneratorException.class)
    public byte[] generateTestImage(CreateTestDiaryRequest request) throws ImageGeneratorException {
        KarloParameter param = request.getKarloParameter();
        try {
            return karloRequestService.getTestImageAsUrl(param);
        } catch (ImageGeneratorException e) {
            promptService.createPrompt(param.getPrompt(), false);
            throw e;
        }
    }
}
