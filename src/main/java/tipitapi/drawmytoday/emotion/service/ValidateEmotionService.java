package tipitapi.drawmytoday.emotion.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.emotion.domain.Emotion;
import tipitapi.drawmytoday.emotion.exception.EmotionNotFoundException;
import tipitapi.drawmytoday.emotion.repository.EmotionRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ValidateEmotionService {

    private final EmotionRepository emotionRepository;

    public Emotion validateEmotionById(Long emotionId) {
        return emotionRepository.findById(emotionId).orElseThrow(EmotionNotFoundException::new);
    }
}
