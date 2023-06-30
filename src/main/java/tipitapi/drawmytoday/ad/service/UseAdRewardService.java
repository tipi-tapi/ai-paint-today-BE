package tipitapi.drawmytoday.ad.service;

import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.ad.domain.AdReward;
import tipitapi.drawmytoday.ad.repository.AdRewardRepository;
import tipitapi.drawmytoday.user.domain.User;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UseAdRewardService {

    private final AdRewardRepository adRewardRepository;

    public boolean useReward(User user) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusWeeks(1);
        Optional<AdReward> adReward = adRewardRepository.findValidAdReward(user.getUserId(),
            startDate, endDate);
        if (adReward.isPresent()) {
            adReward.get().useReward();
            return true;
        }
        return false;
    }

}
