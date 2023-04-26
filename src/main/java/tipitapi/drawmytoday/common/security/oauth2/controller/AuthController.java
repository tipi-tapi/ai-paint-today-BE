package tipitapi.drawmytoday.common.security.oauth2.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tipitapi.drawmytoday.common.exception.ErrorCode;
import tipitapi.drawmytoday.common.resolver.AuthUser;
import tipitapi.drawmytoday.common.security.jwt.JwtProperties;
import tipitapi.drawmytoday.common.security.jwt.JwtTokenInfo;
import tipitapi.drawmytoday.common.security.jwt.JwtTokenProvider;
import tipitapi.drawmytoday.common.security.jwt.exception.InvalidTokenException;
import tipitapi.drawmytoday.common.security.jwt.exception.JwtTokenNotFoundException;
import tipitapi.drawmytoday.common.security.oauth2.dto.RequestAppleLogin;
import tipitapi.drawmytoday.common.security.oauth2.dto.ResponseJwtToken;
import tipitapi.drawmytoday.common.security.oauth2.service.AppleOAuthService;
import tipitapi.drawmytoday.common.security.oauth2.service.GoogleOAuthService;
import tipitapi.drawmytoday.user.domain.OAuthType;
import tipitapi.drawmytoday.user.domain.User;
import tipitapi.drawmytoday.user.repository.UserRepository;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final GoogleOAuthService googleOAuthService;
    private final AppleOAuthService appleOAuthService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @PostMapping(value = "/oauth2/google/login")
    @ResponseStatus(HttpStatus.OK)
    public ResponseJwtToken googleLogin(HttpServletRequest request) throws JsonProcessingException {
        log.info("google login");
        ResponseJwtToken jwtToken = googleOAuthService.login(request);
        log.info("jwtToken = {}", jwtToken);
        return jwtToken;
    }

    @PostMapping(value = "/oauth2/apple/login")
    @ResponseStatus(HttpStatus.OK)
    public ResponseJwtToken appleLogin(HttpServletRequest request,
        @RequestBody RequestAppleLogin requestAppleLogin)
        throws IOException {
        log.info("apple login");
        ResponseJwtToken jwtToken = appleOAuthService.login(request, requestAppleLogin);
        return jwtToken;
    }

    // @GetMapping("/logout")
    // @ResponseStatus(HttpStatus.OK)
    // public void logout() {
    // 	log.info("logout");
    // 	JwtCookieProvider.deleteCookieFromRequest(JwtProperties.ACCESS_TOKEN_HEADER,
    // 		JwtProperties.REFRESH_TOKEN_HEADER);
    // }

    @GetMapping("/refresh")
    public ResponseJwtToken getAccessToken(HttpServletRequest request) {
        log.info("refresh token");
        String refreshToken = getRefreshToken(request);

        jwtTokenProvider.validRefreshToken(refreshToken);

        String accessToken = jwtTokenProvider.createNewAccessTokenFromRefreshToken(refreshToken);

        return ResponseJwtToken.of(accessToken, "");
    }

    @DeleteMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    public void deleteAccount(@AuthUser JwtTokenInfo tokenInfo) {
        log.info("delete account");
        User user = userRepository.findById(tokenInfo.getUserId()).orElseThrow(
            () -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다. id=" + tokenInfo.getUserId())
        );

        if (user.getOauthType() == OAuthType.GOOGLE) {
            googleOAuthService.deleteAccount(user);
        } else if (user.getOauthType() == OAuthType.APPLE) {
            appleOAuthService.deleteAccount(user);
        } else {
            throw new RuntimeException("해당 사용자는 OAuth2 로그인이 아닙니다.");
        }
    }

    private String getRefreshToken(HttpServletRequest request) {
        String authorization = request.getHeader(JwtProperties.REFRESH_TOKEN_HEADER);

        if (Objects.isNull(authorization)) {
            throw new JwtTokenNotFoundException(ErrorCode.JWT_REFRESH_TOKEN_NOT_FOUND);
        }

        String[] tokens = StringUtils.delimitedListToStringArray(authorization, " ");

        if (tokens.length != 2 || !"Bearer".equals(tokens[0])) {
            throw new InvalidTokenException();
        }

        return tokens[1];
    }

}
