package tipitapi.drawmytoday.domain.diary.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import tipitapi.drawmytoday.domain.diary.domain.Diary;

public interface DiaryRepository extends DiaryQueryRepository {

    Diary save(Diary diary);

    List<Diary> saveAll(List<Diary> diaries);

    Optional<Diary> findById(Long diaryId);

    void delete(Diary diary);

    void flush();

    List<Diary> findAllByUserUserIdAndDiaryDateBetween(Long userId, LocalDateTime startMonth,
        LocalDateTime endMonth);

    Optional<Diary> findFirstByUserUserIdOrderByCreatedAtDesc(Long userId);
}
