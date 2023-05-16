package tipitapi.drawmytoday.diary.service;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.diary.domain.Diary;
import tipitapi.drawmytoday.diary.domain.Image;
import tipitapi.drawmytoday.diary.dto.GetDiaryResponse;
import tipitapi.drawmytoday.diary.exception.DiaryNotFoundException;
import tipitapi.drawmytoday.diary.exception.NotOwnerOfDiaryException;
import tipitapi.drawmytoday.diary.repository.DiaryRepository;
import tipitapi.drawmytoday.user.domain.User;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final ImageService imageService;

        Diary diary = diaryRepository.findById(diaryId).orElseThrow(DiaryNotFoundException::new);
        ownedByUser(userId, diary);
        Image image = imageService.getImage(diary);
        return DiaryResponse.of(diary, image, diary.getEmotion());
    }
    public GetDiaryResponse getDiary(User user, Long diaryId) {

    private void ownedByUser(Long userId, Diary diary) {
        if (!Objects.equals(diary.getUser().getUserId(), userId)) {
            throw new NotOwnerOfDiaryException();
        }
    }
}
