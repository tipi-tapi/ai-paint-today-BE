package tipitapi.drawmytoday.domain.adreward.domain;

import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tipitapi.drawmytoday.common.entity.BaseEntity;
import tipitapi.drawmytoday.domain.user.domain.User;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdReward extends BaseEntity {

    private Long adRewardId;

    @NotNull
    private User user;


    private LocalDateTime usedAt;

    public AdReward(User user) {
        this.user = user;
    }

    private AdReward(Long adRewardId, User user, LocalDateTime usedAt, LocalDateTime createdAt) {
        super(createdAt);
        this.adRewardId = adRewardId;
        this.user = user;
        this.usedAt = usedAt;
    }

    public static AdReward restore(Long adRewardId, User user, LocalDateTime usedAt, LocalDateTime createdAt) {
        return new AdReward(adRewardId, user, usedAt, createdAt);
    }

    public void useReward() {
        this.usedAt = LocalDateTime.now();
    }
}
