package tipitapi.drawmytoday.diary.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tipitapi.drawmytoday.diary.domain.Diary;
import tipitapi.drawmytoday.diary.domain.EmotionRecord;

@Repository
public interface EmotionRecordRepository extends JpaRepository<EmotionRecord, Long> {

  List<EmotionRecord> findAllByDiary(Diary diary);
}