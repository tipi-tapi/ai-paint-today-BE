package tipitapi.drawmytoday.domain.oauth.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tipitapi.drawmytoday.common.testdata.TestUser;
import tipitapi.drawmytoday.domain.user.domain.SocialCode;
import tipitapi.drawmytoday.domain.user.domain.User;
import tipitapi.drawmytoday.domain.user.service.ValidateUserService;

@ExtendWith(MockitoExtension.class)
class OAuthServiceTest {

    @Mock
    private GoogleOAuthService googleOAuthService;
    @Mock
    private AppleOAuthService appleOAuthService;
    @Mock
    private ValidateUserService validateUserService;
    @InjectMocks
    private OAuthService oAuthService;

    @Nested
    @DisplayName("deleteAccount 메소드 테스트")
    class DeleteAccountTest {

        @Test
        @DisplayName("유저의 소셜 코드가 구글일 경우 구글 OAuth 서비스의 deleteAccount 메소드를 호출한다.")
        void google_user_calls_google_oauth_service() {
            // given
            Long userId = 1L;
            given(validateUserService.validateUserById(userId))
                .willReturn(TestUser.createUserWithSocialCode(SocialCode.GOOGLE));
            // when
            oAuthService.deleteAccount(userId);
            // then
            verify(googleOAuthService).deleteAccount(any(User.class));
        }

        @Test
        @DisplayName("유저의 소셜 코드가 애플일 경우 애플 OAuth 서비스의 deleteAccount 메소드를 호출한다.")
        void apple_user_calls_apple_oauth_service() {
            // given
            Long userId = 1L;
            given(validateUserService.validateUserById(userId))
                .willReturn(TestUser.createUserWithSocialCode(SocialCode.APPLE));
            // when
            oAuthService.deleteAccount(userId);
            // then
            verify(appleOAuthService).deleteAccount(any(User.class));
        }
    }

}