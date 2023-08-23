package tipitapi.drawmytoday.domain.emotion.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.domain.emotion.repository.EmotionRepository;
import tipitapi.drawmytoday.domain.emotion.domain.Emotion;
import tipitapi.drawmytoday.domain.emotion.exception.EmotionNotFoundException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ValidateEmotionService {

    private final EmotionRepository emotionRepository;

    public Emotion validateEmotionById(Long emotionId) {
        return emotionRepository.findById(emotionId).orElseThrow(EmotionNotFoundException::new);
    }
}
