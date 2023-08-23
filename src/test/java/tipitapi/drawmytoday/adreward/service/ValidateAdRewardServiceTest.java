package tipitapi.drawmytoday.adreward.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static tipitapi.drawmytoday.common.testdata.TestUser.createUserWithId;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tipitapi.drawmytoday.domain.adreward.domain.AdReward;
import tipitapi.drawmytoday.domain.adreward.repository.AdRewardRepository;
import tipitapi.drawmytoday.domain.adreward.service.ValidateAdRewardService;
import tipitapi.drawmytoday.domain.user.domain.User;

@ExtendWith(MockitoExtension.class)
class ValidateAdRewardServiceTest {

    @Mock
    AdRewardRepository adRewardRepository;
    @InjectMocks
    ValidateAdRewardService validateAdRewardService;

    @Nested
    @DisplayName("findValidAdReward 메소드 테스트")
    class FindValidateAdRewardTest {

        @Nested
        @DisplayName("유효한 광고리워드가 존재하지 않을 경우")
        class If_no_valid_ad_reward_exists {

            @Test
            @DisplayName("null를 반환한다.")
            void return_null() {
                given(adRewardRepository.findValidAdReward(anyLong(), any(), any()))
                    .willReturn(new ArrayList<>());

                Optional<AdReward> adReward = validateAdRewardService.findValidAdReward(1L);

                assertThat(adReward).isEmpty();
            }
        }

        @Nested
        @DisplayName("유효한 광고리워드가 존재할 경우")
        class If_valid_ad_reward_exists {

            @Test
            @DisplayName("true를 반환한다.")
            void return_adreward() {
                User user = createUserWithId(1L);
                AdReward adReward = new AdReward(user);
                given(adRewardRepository.findValidAdReward(anyLong(), any(), any()))
                    .willReturn(List.of(adReward));

                Optional<AdReward> result = validateAdRewardService.findValidAdReward(1L);

                assertThat(result).isPresent();
                assertThat(result.get().getAdRewardId()).isEqualTo(adReward.getAdRewardId());
            }
        }
    }
}