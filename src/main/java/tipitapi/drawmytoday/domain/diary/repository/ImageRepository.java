package tipitapi.drawmytoday.domain.diary.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import tipitapi.drawmytoday.domain.diary.domain.Image;

public interface ImageRepository extends JpaRepository<Image, Long>, ImageQueryRepository {

    List<Image> findAllByDiaryDiaryId(Long diaryId);
}