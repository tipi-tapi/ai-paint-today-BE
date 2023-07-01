package tipitapi.drawmytoday.adreward.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.adreward.domain.AdReward;
import tipitapi.drawmytoday.adreward.domain.AdType;
import tipitapi.drawmytoday.adreward.repository.AdRewardRepository;
import tipitapi.drawmytoday.user.domain.User;
import tipitapi.drawmytoday.user.service.ValidateUserService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdRewardService {

    private final AdRewardRepository adRewardRepository;
    private final ValidateUserService validateUserService;

    public void createAdReward(Long userId, AdType adType) {
        User user = validateUserService.validateUserById(userId);
        AdReward adReward = AdReward.builder().user(user).adType(adType).build();
        adRewardRepository.save(adReward);
    }
}
