package tipitapi.drawmytoday.domain.generator.domain.dalle.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.domain.diary.domain.Prompt;
import tipitapi.drawmytoday.domain.diary.service.PromptService;
import tipitapi.drawmytoday.domain.diary.service.PromptTextService;
import tipitapi.drawmytoday.domain.emotion.domain.Emotion;
import tipitapi.drawmytoday.domain.generator.domain.dalle.exception.DallEPolicyViolationException;
import tipitapi.drawmytoday.domain.generator.domain.dalle.exception.DallERequestFailException;
import tipitapi.drawmytoday.domain.generator.dto.GeneratedImageAndPrompt;
import tipitapi.drawmytoday.domain.generator.exception.ImageGeneratorException;
import tipitapi.drawmytoday.domain.generator.service.ImageGeneratorService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DallEService implements ImageGeneratorService {

    private final PromptTextService promptTextService;
    private final PromptService promptService;
    private final DallERequestService dalleRequestService;

    @Transactional(noRollbackFor = ImageGeneratorException.class)
    public GeneratedImageAndPrompt generateImage(Emotion emotion, String keyword)
        throws ImageGeneratorException {
        String prompt = promptTextService.createPromptText(emotion, keyword);
        try {
            byte[] image = dalleRequestService.getImageAsUrl(prompt);
            return new GeneratedImageAndPrompt(prompt, image);
        } catch (DallEPolicyViolationException e) {
            promptService.createPrompt(prompt, false);
            return reGenerateImage(emotion);
        } catch (ImageGeneratorException e) {
            promptService.createPrompt(prompt, false);
            throw e;
        }
    }

    private GeneratedImageAndPrompt reGenerateImage(Emotion emotion)
        throws ImageGeneratorException {
        String prompt = promptTextService.createPromptText(emotion, null);
        try {
            byte[] image = dalleRequestService.getImageAsUrl(prompt);
            return new GeneratedImageAndPrompt(prompt, image);
        } catch (ImageGeneratorException e) {
            promptService.createPrompt(prompt, false);
            throw (e instanceof DallEPolicyViolationException) ?
                DallERequestFailException.violatePolicy() : e;
        }
    }


    @Transactional(noRollbackFor = ImageGeneratorException.class)
    public GeneratedImageAndPrompt generateImage(Prompt prompt) throws ImageGeneratorException {
        try {
            byte[] image = dalleRequestService.getImageAsUrl(prompt.getPromptText());
            return new GeneratedImageAndPrompt(prompt.getPromptText(), image);
        } catch (ImageGeneratorException e) {
            promptService.createPrompt(prompt.getPromptText(), false);
            throw (e instanceof DallEPolicyViolationException) ?
                DallERequestFailException.violatePolicy() : e;
        }
    }
}
