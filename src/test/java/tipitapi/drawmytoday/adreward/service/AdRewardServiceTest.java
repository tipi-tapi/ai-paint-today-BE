package tipitapi.drawmytoday.adreward.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static tipitapi.drawmytoday.common.testdata.TestUser.createUser;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tipitapi.drawmytoday.adreward.domain.AdReward;
import tipitapi.drawmytoday.adreward.repository.AdRewardRepository;
import tipitapi.drawmytoday.ticket.service.TicketService;
import tipitapi.drawmytoday.user.domain.User;
import tipitapi.drawmytoday.user.exception.UserNotFoundException;
import tipitapi.drawmytoday.user.service.ValidateUserService;

@ExtendWith(MockitoExtension.class)
class AdRewardServiceTest {

    @Mock
    AdRewardRepository adRewardRepository;
    @Mock
    ValidateUserService validateUserService;
    @Mock
    TicketService ticketService;
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

                assertThatThrownBy(() -> adRewardService.createAdReward(1L))
                    .isInstanceOf(UserNotFoundException.class);
            }
        }

        @Nested
        @DisplayName("userId에 해당하는 유저가 존재할 경우")
        class If_user_exists {

            @Test
            @DisplayName("AdReward와 Ticket을 생성한다.")
            void create_AdReward_and_Ticket() {
                User user = createUser();
                given(validateUserService.validateUserById(anyLong())).willReturn(user);

                adRewardService.createAdReward(1L);

                verify(adRewardRepository, times(1)).save(any(AdReward.class));
                verify(ticketService, times(1)).createTicketByAdReward(eq(user));
            }
        }
    }
}