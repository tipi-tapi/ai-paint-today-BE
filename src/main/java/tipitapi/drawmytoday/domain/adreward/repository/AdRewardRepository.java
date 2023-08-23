package tipitapi.drawmytoday.domain.adreward.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tipitapi.drawmytoday.domain.adreward.domain.AdReward;

public interface AdRewardRepository extends JpaRepository<AdReward, Long> {

    @Query("select a from AdReward a where a.user.userId = :userId and a.usedAt is null "
        + "and a.createdAt between :startDate and :endDate order by a.createdAt asc")
    List<AdReward> findValidAdReward(@Param("userId") Long userId,
        @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
