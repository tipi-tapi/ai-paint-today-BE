package tipitapi.drawmytoday.adreward.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static tipitapi.drawmytoday.common.testdata.TestUser.createUserWithId;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tipitapi.drawmytoday.adreward.domain.AdReward;
import tipitapi.drawmytoday.user.domain.User;

@ExtendWith(MockitoExtension.class)
class UseAdRewardServiceTest {

    @Mock
    ValidateAdRewardService validateAdRewardService;
    @InjectMocks
    UseAdRewardService useAdRewardService;

    @Nested
    @DisplayName("useReward 메소드 테스트")
    class UseRewardTest {

        @Nested
        @DisplayName("유효한 광고리워드가 존재하지 않을 경우")
        class If_no_valid_ad_reward_exists {

            @Test
            @DisplayName("false를 반환한다.")
            void return_false() {
                given(validateAdRewardService.findValidAdReward(anyLong()))
                    .willReturn(Optional.empty());

                boolean used = useAdRewardService.useReward(1L);

                assertThat(used).isFalse();
            }
        }

        @Nested
        @DisplayName("유효한 광고리워드가 존재할 경우")
        class If_valid_ad_reward_exists {

            @Test
            @DisplayName("true를 반환한다.")
            void return_true() {
                User user = createUserWithId(1L);
                AdReward adReward = new AdReward(user);
                given(validateAdRewardService.findValidAdReward(anyLong()))
                    .willReturn(Optional.of(adReward));

                boolean used = useAdRewardService.useReward(1L);

                assertThat(used).isTrue();
            }
        }
    }
}