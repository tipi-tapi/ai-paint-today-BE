package tipitapi.drawmytoday.domain.diary.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.domain.diary.domain.Prompt;
import tipitapi.drawmytoday.domain.diary.exception.PromptNotFoundException;
import tipitapi.drawmytoday.domain.diary.repository.PromptRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ValidatePromptService {

    private final PromptRepository promptRepository;

    public Prompt validatePromptByImageId(Long imageId) {
        return promptRepository.findByImageId(imageId)
            .orElseThrow(PromptNotFoundException::new);
    }

    public Prompt validatePromptById(Long promptId) {
        return promptRepository.findById(promptId)
            .orElseThrow(PromptNotFoundException::new);
    }
}
