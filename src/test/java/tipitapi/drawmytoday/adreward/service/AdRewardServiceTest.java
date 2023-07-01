package tipitapi.drawmytoday.adreward.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tipitapi.drawmytoday.adreward.domain.AdType;
import tipitapi.drawmytoday.adreward.repository.AdRewardRepository;
import tipitapi.drawmytoday.user.exception.UserNotFoundException;
import tipitapi.drawmytoday.user.service.ValidateUserService;

@ExtendWith(MockitoExtension.class)
class AdRewardServiceTest {

    @Mock
    AdRewardRepository adRewardRepository;
    @Mock
    ValidateUserService validateUserService;
    @InjectMocks
    AdRewardService adRewardService;

    @Nested
    @DisplayName("createAdReward 메소드 테스트")
    class CreateAdRewardTest {

        @Nested
        @DisplayName("userId에 해당하는 유저가 존재하지 않을 경우")
        class If_user_not_exists {

            @Test
            @DisplayName("UserNotFoundException 예외를 발생시킨다.")
            void throw_UserNotFoundException() {
                given(validateUserService.validateUserById(anyLong())).willThrow(
                    new UserNotFoundException());

                assertThatThrownBy(() -> adRewardService.createAdReward(1L, AdType.VIDEO))
                    .isInstanceOf(UserNotFoundException.class);
            }
        }
    }
}