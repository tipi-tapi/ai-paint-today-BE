package tipitapi.drawmytoday.domain.diary.service;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.domain.diary.domain.Diary;
import tipitapi.drawmytoday.domain.diary.exception.DiaryNotFoundException;
import tipitapi.drawmytoday.domain.diary.exception.NotOwnerOfDiaryException;
import tipitapi.drawmytoday.domain.diary.repository.DiaryRepository;
import tipitapi.drawmytoday.domain.user.domain.User;

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
        // mocking 이므로 주석 처리
//        if (diaryRepository.getDiaryExistsByDiaryDate(userId, diaryDate).isPresent()) {
//            throw new DiaryDateAlreadyExistsException();
//        }
    }

    private void ownedByUser(Diary diary, User user) {
        if (diary.getUser() != user) {
            throw new NotOwnerOfDiaryException();
        }
    }
}
