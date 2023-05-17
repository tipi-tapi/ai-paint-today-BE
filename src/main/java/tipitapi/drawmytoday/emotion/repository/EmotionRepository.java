package tipitapi.drawmytoday.emotion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tipitapi.drawmytoday.emotion.domain.Emotion;

public interface EmotionRepository extends JpaRepository<Emotion, Long> {

}