package tipitapi.drawmytoday.domain.dalle.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.domain.dalle.dto.GeneratedImageAndPrompt;
import tipitapi.drawmytoday.domain.dalle.exception.DallEException;
import tipitapi.drawmytoday.domain.dalle.exception.DallEPolicyViolationException;
import tipitapi.drawmytoday.domain.dalle.exception.DallERequestFailException;
import tipitapi.drawmytoday.domain.diary.service.PromptService;
import tipitapi.drawmytoday.domain.diary.service.PromptTextService;
import tipitapi.drawmytoday.domain.emotion.domain.Emotion;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DallEService {

    private final PromptTextService promptTextService;
    private final PromptService promptService;
    private final DallERequestService dalleRequestService;

    @Transactional(noRollbackFor = DallEException.class)
    public GeneratedImageAndPrompt generateImage(Emotion emotion, String keyword)
        throws DallEException {
        String prompt = promptTextService.createPromptText(emotion, keyword);
        try {
            byte[] image = dalleRequestService.getImageAsUrl(prompt);
            return new GeneratedImageAndPrompt(prompt, image);
        } catch (DallEPolicyViolationException e) {
            promptService.createPrompt(prompt, false);
            return reGenerateImage(emotion);
        } catch (DallEException e) {
            promptService.createPrompt(prompt, false);
            throw e;
        }
    }

    private GeneratedImageAndPrompt reGenerateImage(Emotion emotion) throws DallEException {
        String prompt = promptTextService.createPromptText(emotion, null);
        try {
            byte[] image = dalleRequestService.getImageAsUrl(prompt);
            return new GeneratedImageAndPrompt(prompt, image);
        } catch (DallEException e) {
            promptService.createPrompt(prompt, false);
            throw (e instanceof DallEPolicyViolationException) ?
                DallERequestFailException.violatePolicy() : e;
        }
    }

}
