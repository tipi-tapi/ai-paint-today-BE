package tipitapi.drawmytoday.dev.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;
import tipitapi.drawmytoday.common.controller.ControllerTestSetup;
import tipitapi.drawmytoday.common.security.jwt.JwtTokenProvider;

@WebMvcTest(DevelopController.class)
class DevelopControllerTest extends ControllerTestSetup {

    private final String BASIC_URL = "/dev";

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Nested
    @DisplayName("getExpiredJwt 메서드는")
    class GetExpiredJwtTest {

        @Test
        @DisplayName("헤더에 jwt 토큰이 없으면 404를 반환한다.")
        void return_404() throws Exception {
            //given
            //when
            ResultActions result = noSecurityMockMvc.perform(get(BASIC_URL + "/expire"));

            //then
            result.andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("헤더에 jwt 토큰이 있으면 만료된 jwt 토큰을 반환한다.")
        void return_expired_jwt_token() throws Exception {
            //given
            String jwtToken = "jwtToken";
            String expiredJwtToken = "expiredJwtToken";
            given(jwtTokenProvider.expireToken(jwtToken)).willReturn(expiredJwtToken);

            //when
            ResultActions result = noSecurityMockMvc.perform(get(BASIC_URL + "/expire")
                .header("Authorization", "Bearer " + jwtToken));

            //then
            result.andExpect(status().isOk())
                .andExpect(r -> r.getResponse().getContentAsString().equals(expiredJwtToken));
        }
    }
}