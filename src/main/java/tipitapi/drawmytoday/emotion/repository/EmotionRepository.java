package tipitapi.drawmytoday.emotion.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tipitapi.drawmytoday.emotion.domain.Emotion;

public interface EmotionRepository extends JpaRepository<Emotion, Long> {

    @Query("select e from Emotion e where e.isActive = true order by e.emotionId asc")
    List<Emotion> findAllActiveEmotions();
}