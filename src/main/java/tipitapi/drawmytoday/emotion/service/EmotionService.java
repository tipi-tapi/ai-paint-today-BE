package tipitapi.drawmytoday.emotion.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.emotion.dto.CreateEmotionRequest;
import tipitapi.drawmytoday.emotion.dto.CreateEmotionResponse;
import tipitapi.drawmytoday.emotion.dto.GetActiveEmotionsResponse;
import tipitapi.drawmytoday.emotion.repository.EmotionRepository;
import tipitapi.drawmytoday.user.service.ValidateUserService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EmotionService {

    private final EmotionRepository emotionRepository;
    private final ValidateUserService validateUserService;


    public List<GetActiveEmotionsResponse> getActiveEmotions(Long userId, String language) {
        validateUserService.validateUserById(userId);
        return GetActiveEmotionsResponse.buildWithEmotions(
            emotionRepository.findAllActiveEmotions(), language);
    }

    @Transactional
    public List<CreateEmotionResponse> createEmotions(
        List<CreateEmotionRequest> createEmotionRequests) {
        return emotionRepository.saveAll(
            createEmotionRequests.stream().map(CreateEmotionRequest::toEmotionEntity).collect(
                Collectors.toList())
        ).stream().map(CreateEmotionResponse::of).collect(Collectors.toList());
    }
}
