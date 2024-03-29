package tipitapi.drawmytoday.domain.diary.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.domain.diary.domain.Diary;
import tipitapi.drawmytoday.domain.diary.domain.Prompt;
import tipitapi.drawmytoday.domain.diary.repository.PromptRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PromptService {

    private final PromptRepository promptRepository;

    public Prompt createPrompt(Diary diary, String prompt, boolean isSuccess) {
        return promptRepository.save(Prompt.create(diary, prompt, isSuccess));
    }

    public Prompt createPrompt(String prompt, boolean isSuccess) {
        return promptRepository.save(Prompt.create(prompt, isSuccess));
    }

    public Optional<Prompt> getPromptByDiaryId(Long diaryId) {
        return promptRepository.findAllByDiaryDiaryIdAndIsSuccessTrue(diaryId)
            .stream().findFirst();
    }
}
