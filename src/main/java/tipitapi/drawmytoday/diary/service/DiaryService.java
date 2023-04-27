package tipitapi.drawmytoday.diary.service;

import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.diary.domain.Diary;
import tipitapi.drawmytoday.diary.domain.EmotionRecord;
import tipitapi.drawmytoday.diary.domain.Image;
import tipitapi.drawmytoday.diary.dto.DiaryResponse;
import tipitapi.drawmytoday.diary.exception.DiaryNotFoundException;
import tipitapi.drawmytoday.diary.exception.NotOwnerOfDiaryException;
import tipitapi.drawmytoday.diary.repository.DiaryRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final ImageService imageService;
    private final EmotionRecordService emotionRecordService;

    public DiaryResponse getDiary(Long userId, Long diaryId) {
        Diary diary = diaryRepository.findById(diaryId).orElseThrow(DiaryNotFoundException::new);
        ownedByUser(userId, diary);
        Image image = imageService.getImage(diary);
        List<EmotionRecord> records = emotionRecordService.getEmotionRecords(diary);
        return DiaryResponse.of(diary, image, records);
    }

    private void ownedByUser(Long userId, Diary diary) {
        if (!Objects.equals(diary.getUser().getUserId(), userId)) {
            throw new NotOwnerOfDiaryException();
        }
    }
}
