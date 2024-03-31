package tipitapi.drawmytoday.domain.diary.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.domain.diary.domain.Prompt;
import tipitapi.drawmytoday.domain.diary.repository.PromptRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PromptService {

    private final PromptRepository promptRepository;

    /**
     * 테스트용 일기 생성 로직 수정 시 Deprecated 처리
     */
    @Deprecated
    public Prompt createPrompt(String promptText, boolean isSuccess) {
        if (isSuccess) {
            Prompt prompt = Prompt.create(promptText);
            prompt.imageGeneratorSuccess();
            return promptRepository.save(prompt);
        }
        return promptRepository.save(Prompt.create(promptText));
    }

    public Optional<Prompt> getPromptByImageId(Long imageId) {
        return promptRepository.findByImageId(imageId);
    }

    public Prompt savePrompt(Prompt prompt) {
        return promptRepository.save(prompt);
    }
}
