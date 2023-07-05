package tipitapi.drawmytoday.adreward.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.adreward.domain.AdReward;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UseAdRewardService {

    private final ValidateAdRewardService validateAdRewardService;

    @Transactional
    public boolean useReward(Long userId) {
        Optional<AdReward> adReward = validateAdRewardService.findValidAdReward(userId);
        if (adReward.isEmpty()) {
            return false;
        }
        adReward.get().useReward();
        return true;
    }

}
