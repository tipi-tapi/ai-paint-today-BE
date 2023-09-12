package tipitapi.drawmytoday.domain.oauth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import tipitapi.drawmytoday.common.exception.BusinessException;
import tipitapi.drawmytoday.common.security.jwt.JwtTokenProvider;
import tipitapi.drawmytoday.common.testdata.TestAuth;
import tipitapi.drawmytoday.common.testdata.TestUser;
import tipitapi.drawmytoday.domain.oauth.dto.AppleIdToken;
import tipitapi.drawmytoday.domain.oauth.dto.OAuthAccessToken;
import tipitapi.drawmytoday.domain.oauth.dto.RequestAppleLogin;
import tipitapi.drawmytoday.domain.oauth.dto.ResponseJwtToken;
import tipitapi.drawmytoday.domain.oauth.properties.AppleProperties;
import tipitapi.drawmytoday.domain.oauth.repository.AuthRepository;
import tipitapi.drawmytoday.domain.user.domain.SocialCode;
import tipitapi.drawmytoday.domain.user.domain.User;
import tipitapi.drawmytoday.domain.user.service.UserService;
import tipitapi.drawmytoday.domain.user.service.ValidateUserService;

@ExtendWith(MockitoExtension.class)
class AppleOAuthServiceTest {

    @Mock
    private AppleProperties appleProperties;
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private UserService userService;
    @Mock
    private ValidateUserService validateUserService;
    @Mock
    private AuthRepository authRepository;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @InjectMocks
    private AppleOAuthService appleOAuthService;

    @Nested
    @DisplayName("login 메서드 테스트")
    class Login_test {

        private final HttpServletRequest request;
        private final RequestAppleLogin requestAppleLogin;

        private Login_test() {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader("Authorization", "Bearer authCode");
            this.request = request;
            requestAppleLogin = new RequestAppleLogin("idToken.idToken");
        }

        @Nested
        @DisplayName("REST 요청으로 받은")
        class Rest_request {

            @BeforeEach
            void setUp() {
                given(appleProperties.getClientId()).willReturn("clientId");
                given(appleProperties.getClientSecret()).willReturn("clientSecret");
                given(appleProperties.getTokenUrl()).willReturn("tokenUrl");
            }

            @Test
            @DisplayName("accessToken 파싱을 실패할 경우 예외를 던진다.")
            void accessToken_parsing_fail_then_throw_exception() throws Exception {
                // given
                given(restTemplate.postForEntity(any(String.class), any(HttpEntity.class),
                    any(Class.class)))
                    .willReturn(ResponseEntity.of(Optional.of("invalid token")));
                given(objectMapper.readValue(any(String.class), any(Class.class)))
                    .willThrow(JsonProcessingException.class);

                // when
                // then
                assertThatThrownBy(() -> appleOAuthService.login(request, requestAppleLogin))
                    .isInstanceOf(BusinessException.class);
            }

            @Test
            @DisplayName("AppleIdToken 파싱을 실패할 경우 예외를 던진다.")
            void appleIdToken_parsing_fail_then_throw_exception() throws Exception {
                // given
                given(restTemplate.postForEntity(any(String.class), any(HttpEntity.class),
                    any(Class.class)))
                    .willReturn(ResponseEntity.of(Optional.of("valid token")));
                given(objectMapper.readValue(any(String.class), any(Class.class)))
                    .willReturn(new OAuthAccessToken());
                given(objectMapper.readValue(any(byte[].class), any(Class.class)))
                    .willThrow(IOException.class);

                // when
                // then
                assertThatThrownBy(() -> appleOAuthService.login(request, requestAppleLogin))
                    .isInstanceOf(BusinessException.class);
            }
        }

        @Nested
        @DisplayName("유저 이메일을 알아낸 뒤")
        class After_find_user_email {

            @BeforeEach
            void setUp() throws Exception {
                given(appleProperties.getClientId()).willReturn("clientId");
                given(appleProperties.getClientSecret()).willReturn("clientSecret");
                given(appleProperties.getTokenUrl()).willReturn("tokenUrl");
                given(restTemplate.postForEntity(any(String.class), any(HttpEntity.class),
                    any(Class.class))).willReturn(ResponseEntity.of(Optional.of("token")));
                given(objectMapper.readValue(any(String.class), any(Class.class)))
                    .willReturn(new OAuthAccessToken(null, 0, "refreshToken", null));
                AppleIdToken appleIdToken = new AppleIdToken();
                ReflectionTestUtils.setField(appleIdToken, "email", "email");
                given(objectMapper.readValue(any(byte[].class), any(Class.class)))
                    .willReturn(appleIdToken);
            }

            @Test
            @DisplayName("유저가 존재하지 않을 경우 회원가입을 진행하고 토큰을 반환한다.")
            void user_not_exist_then_register_and_return_token() throws Exception {
                // given
                User newUser = TestUser.createUserWithId(1L);
                String accessToken = "accessToken";
                String refreshToken = "refreshToken";
                given(validateUserService.validateRegisteredUserByEmail(any(String.class),
                    eq(SocialCode.APPLE))).willReturn(null);
                given(userService.registerUser(any(String.class), eq(SocialCode.APPLE),
                    any(String.class))).willReturn(newUser);
                given(jwtTokenProvider.createAccessToken(
                    eq(newUser.getUserId()), eq(newUser.getUserRole())))
                    .willReturn(accessToken);
                given(jwtTokenProvider.createRefreshToken(
                    eq(newUser.getUserId()), eq(newUser.getUserRole())))
                    .willReturn(refreshToken);

                // when
                ResponseJwtToken responseJwtToken = appleOAuthService.login(request,
                    requestAppleLogin);

                // then
                verify(userService).registerUser(any(String.class), eq(SocialCode.APPLE),
                    any(String.class));
                assertThat(responseJwtToken.getAccessToken()).isEqualTo(accessToken);
                assertThat(responseJwtToken.getRefreshToken()).isEqualTo(refreshToken);
            }

            @Test
            @DisplayName("유저가 존재할 경우 회원가입을 진행하지 않고 토큰을 반환한다.")
            void user_exist_then_no_register() {
                // given
                User user = TestUser.createUserWithId(1L);
                String accessToken = "accessToken";
                String refreshToken = "refreshToken";
                given(validateUserService.validateRegisteredUserByEmail(
                    any(String.class), eq(SocialCode.APPLE))).willReturn(user);
                given(jwtTokenProvider.createAccessToken(
                    eq(user.getUserId()), eq(user.getUserRole()))).willReturn(accessToken);
                given(jwtTokenProvider.createRefreshToken(
                    eq(user.getUserId()), eq(user.getUserRole()))).willReturn(refreshToken);

                // when
                ResponseJwtToken responseJwtToken = appleOAuthService.login(
                    request, requestAppleLogin);

                // then
                verify(userService, never()).registerUser(any(String.class), eq(SocialCode.APPLE),
                    any(String.class));
                assertThat(responseJwtToken.getAccessToken()).isEqualTo(accessToken);
                assertThat(responseJwtToken.getRefreshToken()).isEqualTo(refreshToken);
            }
        }
    }

    @Nested
    @DisplayName("deleteAccount 메서드 테스트")
    class DeleteAccount_test {

        @Test
        @DisplayName("유저 아이디에 해당하는 Auth가 없을 경우 회원탈퇴를 진행하지 않는다.")
        void no_auth_then_no_delete() {
            // given
            User user = TestUser.createUser();
            given(authRepository.findByUser(any(User.class))).willReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> appleOAuthService.deleteAccount(user))
                .isInstanceOf(BusinessException.class);
            verify(restTemplate, never()).postForEntity(any(String.class), any(HttpEntity.class),
                any(Class.class));
            assertThat(user.getDeletedAt()).isNull();
        }

        @Nested
        @DisplayName("유저 아이디에 해당하는 Auth가 있을 경우")
        class Exist_auth {

            @BeforeEach
            void setUp() {
                given(appleProperties.getClientId()).willReturn("clientId");
                given(appleProperties.getClientSecret()).willReturn("clientSecret");
                given(appleProperties.getDeleteAccountUrl()).willReturn("deleteAccountUrl");
            }

            @Test
            @DisplayName("회원탈퇴를 진행한다.")
            void exist_auth_then_delete_user() {
                // given
                User user = TestUser.createUser();
                given(authRepository.findByUser(eq(user))).willReturn(Optional.of(
                    TestAuth.createAuth(user)));
                given(restTemplate.postForObject(any(String.class), any(HttpEntity.class),
                    eq(String.class))).willReturn(null);

                // when
                appleOAuthService.deleteAccount(user);

                // then
                assertThat(user.getDeletedAt()).isNotNull();
            }

            @Test
            @DisplayName("회원탈퇴 REST 요청이 실패할 경우 예외를 던진다.")
            void rest_request_fail_then_throw_exception() {
                // given
                User user = TestUser.createUser();
                given(authRepository.findByUser(eq(user))).willReturn(Optional.of(
                    TestAuth.createAuth(user)));
                given(restTemplate.postForObject(any(String.class), any(HttpEntity.class),
                    eq(String.class))).willReturn(
                    "{\"error\":\"invalid_token\",\"error_description\":\"Invalid Value\"}");

                // when
                // then
                assertThatThrownBy(() -> appleOAuthService.deleteAccount(user))
                    .isInstanceOf(BusinessException.class);
                assertThat(user.getDeletedAt()).isNull();
            }
        }
    }
}