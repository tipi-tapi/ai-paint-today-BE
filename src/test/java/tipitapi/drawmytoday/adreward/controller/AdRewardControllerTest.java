package tipitapi.drawmytoday.adreward.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.ResultActions;
import tipitapi.drawmytoday.domain.adreward.controller.AdRewardController;
import tipitapi.drawmytoday.domain.adreward.service.AdRewardService;
import tipitapi.drawmytoday.common.controller.ControllerTestSetup;
import tipitapi.drawmytoday.common.controller.WithCustomUser;

@WebMvcTest(AdRewardController.class)
@WithCustomUser
class AdRewardControllerTest extends ControllerTestSetup {

    private static final String BASIC_URL = "/ad";

    @MockBean
    private AdRewardService adRewardService;

    @Nested
    @DisplayName("createDiary 메서드는")
    class CreateDiaryMethod {

        @Test
        @DisplayName("정상 흐름이면 광고 기록을 생성하고 No Content를 반환한다")
        void create_ad_reward() throws Exception {
            //given
            //when
            ResultActions result = mockMvc.perform(post(BASIC_URL)
                .with(SecurityMockMvcRequestPostProcessors.csrf()));

            //then
            result.andExpect(status().isNoContent());
            verify(adRewardService).createAdReward(any(Long.class));
        }
    }

}