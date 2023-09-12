package tipitapi.drawmytoday.domain.emotion.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.common.cache.CacheConst;
import tipitapi.drawmytoday.common.converter.Language;
import tipitapi.drawmytoday.domain.emotion.dto.CreateEmotionRequest;
import tipitapi.drawmytoday.domain.emotion.dto.CreateEmotionResponse;
import tipitapi.drawmytoday.domain.emotion.dto.GetActiveEmotionsResponse;
import tipitapi.drawmytoday.domain.emotion.repository.EmotionRepository;
import tipitapi.drawmytoday.domain.user.service.ValidateUserService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EmotionService {

    private final EmotionRepository emotionRepository;
    private final ValidateUserService validateUserService;


    @Cacheable(value = CacheConst.ACTIVE_EMOTIONS, key = "#language")
    public List<GetActiveEmotionsResponse> getActiveEmotions(Long userId, Language language) {
        validateUserService.validateUserById(userId);
        return GetActiveEmotionsResponse.buildWithEmotions(
            emotionRepository.findAllActiveEmotions(), language);
    }

    @Transactional
    @CacheEvict(value = CacheConst.ACTIVE_EMOTIONS, allEntries = true)
    public List<CreateEmotionResponse> createEmotions(
        List<CreateEmotionRequest> createEmotionRequests) {
        return emotionRepository.saveAll(
            createEmotionRequests.stream().map(CreateEmotionRequest::toEmotionEntity).collect(
                Collectors.toList())
        ).stream().map(CreateEmotionResponse::of).collect(Collectors.toList());
    }
}
