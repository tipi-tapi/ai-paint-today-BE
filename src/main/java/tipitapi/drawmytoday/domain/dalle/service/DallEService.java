package tipitapi.drawmytoday.domain.dalle.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.domain.dalle.dto.GeneratedImageAndPrompt;
import tipitapi.drawmytoday.domain.dalle.exception.DallEException;
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

            if (image == null) {
                promptService.createPrompt(prompt, false);
                prompt = promptTextService.createPromptText(emotion, null);
                image = dalleRequestService.getImageAsUrl(prompt);

                if (image == null) { // 재시도하였음에도, 컨텐츠 정책 위반 에러가 발생할경우 이미지 생성 실패 처리
                    throw DallERequestFailException.violatePolicy();
                }
            }

            return new GeneratedImageAndPrompt(prompt, image);
        } catch (DallEException e) {
            promptService.createPrompt(prompt, false);
            throw e;
        }
    }

}
