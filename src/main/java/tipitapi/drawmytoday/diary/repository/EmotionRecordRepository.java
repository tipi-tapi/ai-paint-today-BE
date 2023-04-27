package tipitapi.drawmytoday.diary.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import tipitapi.drawmytoday.diary.domain.Diary;
import tipitapi.drawmytoday.diary.domain.EmotionRecord;

public interface EmotionRecordRepository extends JpaRepository<EmotionRecord, Long> {

    List<EmotionRecord> findAllByDiary(Diary diary);
}