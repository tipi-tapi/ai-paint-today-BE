package tipitapi.drawmytoday.domain.generator.api.dalle.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.domain.diary.domain.Prompt;
import tipitapi.drawmytoday.domain.diary.dto.CreateTestDiaryRequest;
import tipitapi.drawmytoday.domain.diary.service.PromptService;
import tipitapi.drawmytoday.domain.diary.service.PromptTextService;
import tipitapi.drawmytoday.domain.generator.exception.ImageGeneratorException;
import tipitapi.drawmytoday.domain.generator.service.ImageGeneratorService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DallEService implements ImageGeneratorService {

    private final PromptTextService promptTextService;
    private final PromptService promptService;
    private final DallERequestService dalleRequestService;

//    @Transactional(noRollbackFor = ImageGeneratorException.class)
//    public GeneratedImageAndPrompt generateImage(Emotion emotion, String keyword)
//        throws ImageGeneratorException {
//        String prompt = promptTextService.createPrompt(emotion, keyword);
//        try {
//            byte[] image = dalleRequestService.getImageAsUrl(prompt);
//            return new GeneratedImageAndPrompt(prompt, image);
//        } catch (DallEPolicyViolationException e) {
//            promptService.createPrompt(prompt, false);
//            return reGenerateImage(emotion);
//        } catch (ImageGeneratorException e) {
//            promptService.createPrompt(prompt, false);
//            throw e;
//        }
//    }
//
//    private GeneratedImageAndPrompt reGenerateImage(Emotion emotion)
//        throws ImageGeneratorException {
//        String prompt = promptTextService.createPrompt(emotion, null);
//        try {
//            byte[] image = dalleRequestService.getImageAsUrl(prompt);
//            return new GeneratedImageAndPrompt(prompt, image);
//        } catch (ImageGeneratorException e) {
//            promptService.createPrompt(prompt, false);
//            throw (e instanceof DallEPolicyViolationException) ?
//                DallERequestFailException.violatePolicy() : e;
//        }
//    }
//
//
//    @Transactional(noRollbackFor = ImageGeneratorException.class)
//    public GeneratedImageAndPrompt generateImage(Prompt prompt) throws ImageGeneratorException {
//        try {
//            byte[] image = dalleRequestService.getImageAsUrl(prompt.getPromptText());
//            return new GeneratedImageAndPrompt(prompt.getPromptText(), image);
//        } catch (ImageGeneratorException e) {
//            promptService.createPrompt(prompt.getPromptText(), false);
//            throw (e instanceof DallEPolicyViolationException) ?
//                DallERequestFailException.violatePolicy() : e;
//        }
//    }

    @Override
    public byte[] generateImage(Prompt prompt) throws ImageGeneratorException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<byte[]> generateTestImage(CreateTestDiaryRequest request)
        throws ImageGeneratorException {
        throw new UnsupportedOperationException();
    }
}
