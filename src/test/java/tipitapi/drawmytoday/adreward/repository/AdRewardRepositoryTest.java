package tipitapi.drawmytoday.adreward.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import tipitapi.drawmytoday.ad.domain.AdReward;
import tipitapi.drawmytoday.ad.domain.AdType;
import tipitapi.drawmytoday.ad.repository.AdRewardRepository;
import tipitapi.drawmytoday.common.BaseRepositoryTest;
import tipitapi.drawmytoday.user.domain.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class AdRewardRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private AdRewardRepository adRewardRepository;

    @Nested
    @DisplayName("findValidAdReward 메소드 테스트")
    class FindValidAdRewardTest {

        @Nested
        @DisplayName("등록된 광고리워드가 없을 경우")
        class If_no_ad_reward_exists {

            @Test
            @DisplayName("null을 반환한다.")
            void return_null() {
                User user = createUser();

                List<AdReward> foundAdReward = adRewardRepository.findValidAdReward(
                    user.getUserId(), LocalDateTime.now().minusWeeks(1), LocalDateTime.now());

                assertThat(foundAdReward).isEmpty();
            }
        }

        @Nested
        @DisplayName("등록된 광고리워드가 사용된 상태인 경우")
        class If_ad_reward_is_used {

            @Test
            @DisplayName("null을 반환한다.")
            void return_null() {
                User user = createUser();
                AdReward adReward = new AdReward(user, AdType.VIDEO);
                adReward.useReward();
                adRewardRepository.save(adReward);

                List<AdReward> foundAdReward = adRewardRepository.findValidAdReward(
                    user.getUserId(), LocalDateTime.now().minusWeeks(1), LocalDateTime.now());

                assertThat(foundAdReward).isEmpty();
            }
        }

        @Nested
        @DisplayName("등록된 광고리워드가 만료된 경우")
        class If_ad_reward_is_expired {

            @Test
            @DisplayName("null을 반환한다.")
            @Sql("ExpiredAdReward.sql")
            void return_null() {
                final Long userId = 1L;
                final LocalDateTime startDate = LocalDateTime.of(2023, 6, 1, 9, 0);
                final LocalDateTime endDate = LocalDateTime.of(2023, 6, 30, 9, 0);

                List<AdReward> foundAdReward =
                    adRewardRepository.findValidAdReward(userId, startDate, endDate);

                assertThat(foundAdReward).isEmpty();
            }
        }

        @Nested
        @DisplayName("유효한 광고리워드가 존재하는 경우")
        class If_valid_ad_reward_exists {

            @Nested
            @DisplayName("여러개가 존재할 경우")
            class If_multiple_valid_ad_reward_exists {

                @Test
                @DisplayName("가장 오래된 광고리워드를 반환한다.")
                @Sql("ValidAdReward.sql")
                void return_oldest_ad_reward() {
                    final Long userId = 1L;
                    final LocalDateTime startDate = LocalDateTime.of(2023, 6, 1, 9, 0);
                    final LocalDateTime endDate = LocalDateTime.of(2023, 6, 30, 9, 0);

                    List<AdReward> foundAdReward =
                        adRewardRepository.findValidAdReward(userId, startDate, endDate);

                    assertThat(foundAdReward.isEmpty()).isFalse();
                    assertThat(foundAdReward.size()).isEqualTo(2);
                    assertThat(foundAdReward.get(0).getAdRewardId()).isEqualTo(3L);
                    assertThat(foundAdReward.get(0).getUsedAt()).isNull();
                }
            }
        }
    }

}
