package tipitapi.drawmytoday.diary.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tipitapi.drawmytoday.diary.domain.Diary;
import tipitapi.drawmytoday.diary.domain.Image;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

  Optional<Image> findByIsSelectedTrueAndDiary(Diary diary);
}