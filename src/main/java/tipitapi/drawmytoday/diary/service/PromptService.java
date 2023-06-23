package tipitapi.drawmytoday.diary.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.common.exception.BusinessException;
import tipitapi.drawmytoday.common.exception.ErrorCode;
import tipitapi.drawmytoday.diary.domain.Diary;
import tipitapi.drawmytoday.diary.domain.Prompt;
import tipitapi.drawmytoday.diary.exception.PromptNotFoundException;
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

    @Transactional(noRollbackFor = {PromptNotFoundException.class, BusinessException.class})
    public Prompt getOnePromptByDiaryId(Long diaryId) {
        List<Prompt> prompts = promptRepository.findAllByDiaryDiaryId(diaryId);
        if (prompts.isEmpty()) {
            throw new PromptNotFoundException();
        } else if (prompts.size() > 1) {
            throw new BusinessException(ErrorCode.PROMPT_NOT_UNIQUE);
        }
        return prompts.get(0);
    }
}
