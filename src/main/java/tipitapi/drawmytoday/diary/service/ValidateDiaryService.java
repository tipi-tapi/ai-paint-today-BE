package tipitapi.drawmytoday.diary.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.diary.domain.Diary;
import tipitapi.drawmytoday.diary.exception.DiaryNotFoundException;
import tipitapi.drawmytoday.diary.exception.NotOwnerOfDiaryException;
import tipitapi.drawmytoday.diary.repository.DiaryRepository;
import tipitapi.drawmytoday.user.domain.User;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ValidateDiaryService {

    private final DiaryRepository diaryRepository;

    public Diary validateDiaryById(Long diaryId, User user) {
        Diary diary = diaryRepository.findFirstByDiaryId(diaryId)
            .orElseThrow(DiaryNotFoundException::new);
        ownedByUser(diary, user);
        return diary;
    }

    private void ownedByUser(Diary diary, User user) {
        if (diary.getUser() != user) {
            throw new NotOwnerOfDiaryException();
        }
    }
}
