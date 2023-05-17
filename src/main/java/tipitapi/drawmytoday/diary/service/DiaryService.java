package tipitapi.drawmytoday.diary.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.diary.domain.Diary;
import tipitapi.drawmytoday.diary.domain.Image;
import tipitapi.drawmytoday.diary.dto.GetDiaryResponse;
import tipitapi.drawmytoday.diary.exception.DiaryNotFoundException;
import tipitapi.drawmytoday.diary.repository.DiaryRepository;
import tipitapi.drawmytoday.user.domain.User;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final ImageService imageService;

    public GetDiaryResponse getDiary(User user, Long diaryId) {
        Diary diary = diaryRepository.findByDiaryIdAndUser(diaryId, user)
            .orElseThrow(DiaryNotFoundException::new);

        Image image = imageService.getImage(diary);
        return GetDiaryResponse.of(diary, image, diary.getEmotion());
    }
}
