package tipitapi.drawmytoday.domain.emotion.repository;

import tipitapi.drawmytoday.domain.emotion.domain.Emotion;
import java.util.List;
import java.util.Optional;

public interface EmotionRepository {
    List<Emotion> findAllActiveEmotions();
    List<Emotion> saveAll(List<Emotion> emotions);
    Optional<Emotion> findById(Long emotionId);
    Emotion save(Emotion emotion);
}
