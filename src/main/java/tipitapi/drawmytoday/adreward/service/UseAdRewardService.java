package tipitapi.drawmytoday.adreward.service;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.adreward.domain.AdReward;
import tipitapi.drawmytoday.adreward.repository.AdRewardRepository;
import tipitapi.drawmytoday.user.domain.User;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UseAdRewardService {

    private final AdRewardRepository adRewardRepository;

    @Transactional
    public boolean useReward(User user) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusWeeks(1);
        List<AdReward> adReward = adRewardRepository.findValidAdReward(user.getUserId(),
            startDate, endDate);
        if (adReward.isEmpty()) {
            return false;
        }
        adReward.get(0).useReward();
        return true;
    }

}
