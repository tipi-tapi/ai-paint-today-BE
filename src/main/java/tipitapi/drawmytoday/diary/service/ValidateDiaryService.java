package tipitapi.drawmytoday.diary.service;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.diary.domain.Diary;
import tipitapi.drawmytoday.diary.exception.DiaryDateAlreadyExistsException;
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
        Diary diary = diaryRepository.findById(diaryId)
            .orElseThrow(DiaryNotFoundException::new);
        ownedByUser(diary, user);
        return diary;
    }

    public void validateExistsByDate(Long userId, LocalDate diaryDate) {
        if (diaryRepository.getDiaryExistsByDiaryDate(userId, diaryDate).isPresent()) {
            throw new DiaryDateAlreadyExistsException();
        }
    }

    private void ownedByUser(Diary diary, User user) {
        if (diary.getUser() != user) {
            throw new NotOwnerOfDiaryException();
        }
    }
}
