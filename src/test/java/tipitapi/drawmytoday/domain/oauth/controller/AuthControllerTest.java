package tipitapi.drawmytoday.domain.oauth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.ResultActions;
import tipitapi.drawmytoday.common.controller.ControllerTestSetup;
import tipitapi.drawmytoday.common.controller.WithCustomUser;
import tipitapi.drawmytoday.common.security.jwt.JwtTokenProvider;
import tipitapi.drawmytoday.domain.oauth.dto.RequestAppleLogin;
import tipitapi.drawmytoday.domain.oauth.dto.ResponseJwtToken;
import tipitapi.drawmytoday.domain.oauth.service.AppleOAuthService;
import tipitapi.drawmytoday.domain.oauth.service.GoogleOAuthService;
import tipitapi.drawmytoday.domain.oauth.service.OAuthService;

@WebMvcTest(AuthController.class)
class AuthControllerTest extends ControllerTestSetup {

    private final String BASIC_URL = "/oauth2";

    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private OAuthService oAuthService;
    @MockBean
    private GoogleOAuthService googleOAuthService;
    @MockBean
    private AppleOAuthService appleOAuthService;
    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Nested
    @DisplayName("googleLogin 메서드는")
    class GoogleLoginTest {

        @Test
        @DisplayName("정상 흐름이면 access token과 refresh token을 발급한다.")
        void return_access_token_and_refresh_token() throws Exception {
            //given
            String authCode = "authCode";
            String jwtAccessToken = "jwtAccessToken";
            String jwtRefreshToken = "jwtRefreshToken";
            given(googleOAuthService.login(any(HttpServletRequest.class)))
                .willReturn(ResponseJwtToken.of(jwtAccessToken, jwtRefreshToken));

            //when
            ResultActions result = noSecurityMockMvc.perform(post(BASIC_URL + "/google/login")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .header("Authorization", "Bearer " + authCode));

            //then
            result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value(jwtAccessToken))
                .andExpect(jsonPath("$.data.refreshToken").value(jwtRefreshToken));
        }
    }

    @Nested
    @DisplayName("appleLogin 메서드는")
    class AppleLoginTest {

        private final String authCode = "authCode";

        @Test
        @DisplayName("정상 흐름이면 access token과 refresh token을 발급한다.")
        void return_access_token_and_refresh_token() throws Exception {
            // given
            String jwtAccessToken = "jwtAccessToken";
            String jwtRefreshToken = "jwtRefreshToken";
            given(appleOAuthService.login(any(HttpServletRequest.class),
                any(RequestAppleLogin.class)))
                .willReturn(ResponseJwtToken.of(jwtAccessToken, jwtRefreshToken));

            //when
            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("idToken", "idToken");
            ResultActions result = noSecurityMockMvc.perform(post(BASIC_URL + "/apple/login")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .header("Authorization", "Bearer " + authCode)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(requestMap)));

            //then
            result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value(jwtAccessToken))
                .andExpect(jsonPath("$.data.refreshToken").value(jwtRefreshToken));
        }

        @Nested
        @DisplayName("idToken이")
        class Id_token_is {

            @Test
            @DisplayName("null이면 400 에러를 발생시킨다.")
            void null_than_return_bad_request() throws Exception {
                //given
                //when
                Map<String, Object> requestMap = new HashMap<>();
                requestMap.put("idToken", null);
                ResultActions result = noSecurityMockMvc.perform(
                    post(BASIC_URL + "/apple/login")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .header("Authorization", "Bearer " + authCode)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestMap)));

                //then
                result.andExpect(status().isBadRequest());
            }
        }
    }

    @Nested
    @DisplayName("getAccessToken 메서드는")
    class GetAccessTokenTest {

        @Test
        @DisplayName("정상 흐름이면 access token을 발급한다.")
        void return_access_token() throws Exception {
            //given
            String jwtAccessToken = "jwtAccessToken";
            given(jwtTokenProvider.createNewAccessTokenFromRefreshToken(any(String.class)))
                .willReturn(jwtAccessToken);

            //when
            ResultActions result = noSecurityMockMvc.perform(get(BASIC_URL + "/refresh")
                .header("Authorization", "Bearer " + "refreshToken"));

            //then
            result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value(jwtAccessToken));
            verify(jwtTokenProvider).validRefreshToken(any(String.class));
        }
    }

    @Nested
    @WithCustomUser
    @DisplayName("deleteAccount 메서드는")
    class DeleteAccountTest {

        @Test
        @DisplayName("정상 흐름이면 계정을 삭제하고 204를 반환한다.")
        void delete_account() throws Exception {
            //given
            //when
            ResultActions result = mockMvc.perform(delete(BASIC_URL + "/users")
                .with(SecurityMockMvcRequestPostProcessors.csrf()));

            //then
            result.andExpect(status().isNoContent());
            verify(oAuthService).deleteAccount(any(Long.class));
        }
    }
}