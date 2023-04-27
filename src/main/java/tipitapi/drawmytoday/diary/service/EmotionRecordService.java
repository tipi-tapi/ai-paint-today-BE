package tipitapi.drawmytoday.diary.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.diary.domain.Diary;
import tipitapi.drawmytoday.diary.domain.EmotionRecord;
import tipitapi.drawmytoday.diary.repository.EmotionRecordRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EmotionRecordService {

    private final EmotionRecordRepository recordRepository;

    public List<EmotionRecord> getEmotionRecords(Diary diary) {
        return recordRepository.findAllByDiary(diary);
    }
}
