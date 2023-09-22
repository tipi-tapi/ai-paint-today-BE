package tipitapi.drawmytoday.domain.diary.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import tipitapi.drawmytoday.domain.diary.domain.Diary;
import tipitapi.drawmytoday.domain.diary.domain.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {

    Optional<Image> findByIsSelectedTrueAndDiary(Diary diary);

    List<Image> findAllByDiaryDiaryId(Long diaryId);

    List<Image> findAllByDiaryDiaryIdOrderByCreatedAtDesc(Long diaryId);
}