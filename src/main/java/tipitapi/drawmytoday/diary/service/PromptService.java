package tipitapi.drawmytoday.diary.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.diary.domain.Diary;
import tipitapi.drawmytoday.diary.domain.Prompt;
import tipitapi.drawmytoday.diary.repository.PromptRepository;

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

    public Prompt getOnePromptByDiaryId(Long diaryId) {
        List<Prompt> prompts = promptRepository.findAllByDiaryDiaryId(diaryId);
        if (prompts.isEmpty()) {
            prompts.add(Prompt.create("저장된 프롬프트가 없습니다.", true));
        }
        return prompts.get(0);
    }
}
