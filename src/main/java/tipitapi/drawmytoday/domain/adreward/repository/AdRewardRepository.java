package tipitapi.drawmytoday.domain.adreward.repository;

import java.time.LocalDateTime;
import java.util.List;
import tipitapi.drawmytoday.domain.adreward.domain.AdReward;

public interface AdRewardRepository {

    AdReward save(AdReward adReward);

    List<AdReward> findAllByUserId(Long userId);

    List<AdReward> findAllByUserIdAndUsedAtIsNull(Long userId);

    List<AdReward> findValidAdReward(Long userId, LocalDateTime startDate, LocalDateTime endDate);
}
