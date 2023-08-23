package tipitapi.drawmytoday.health.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.head;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.ResultActions;
import tipitapi.drawmytoday.common.controller.ControllerTestSetup;
import tipitapi.drawmytoday.domain.health.controller.HealthCheckController;

@WebMvcTest(HealthCheckController.class)
class HealthCheckControllerTest extends ControllerTestSetup {

    private final String BASIC_URL = "/health";

    @Nested
    @DisplayName("verifyServerAlive 메서드는")
    class VerifyServerAliveTest {

        @Test
        @DisplayName("정상 흐름이면 no content를 반환한다.")
        void return_no_content() throws Exception {
            //given
            //when
            ResultActions result = noSecurityMockMvc.perform(head(BASIC_URL + "/server"));

            //then
            result.andExpect(status().isNoContent());
        }
    }

}