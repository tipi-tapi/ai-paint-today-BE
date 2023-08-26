package tipitapi.drawmytoday.domain.adreward.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tipitapi.drawmytoday.domain.adreward.domain.AdReward;
import tipitapi.drawmytoday.domain.adreward.repository.AdRewardRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ValidateAdRewardService {

    private final AdRewardRepository adRewardRepository;

    public Optional<AdReward> findValidAdReward(Long userId) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusHours(1);
        List<AdReward> adReward = adRewardRepository.findValidAdReward(userId, startDate, endDate);
        if (adReward.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(adReward.get(0));
    }
}
