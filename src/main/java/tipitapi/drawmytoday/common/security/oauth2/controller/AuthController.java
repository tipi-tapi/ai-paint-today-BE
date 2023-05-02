package tipitapi.drawmytoday.common.security.oauth2.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
import org.springframework.web.bind.annotation.RequestMapping;
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
@RequestMapping("/oauth2")
@RequiredArgsConstructor
public class AuthController {

    private final GoogleOAuthService googleOAuthService;
    private final AppleOAuthService appleOAuthService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;


    @Operation(summary = "구글 로그인", description = "프론트로부터 Authorization code를 받아 구글 로그인을 진행합니다.")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "구글 로그인 성공"),
        @ApiResponse(
            responseCode = "400",
            description = "C001 : Authorization header 값이 Bearer 토큰이 아니거나 없습니다.",
            content = @Content(schema = @Schema(hidden = true)))
    })
    @PostMapping(value = "/google/login")
    @ResponseStatus(HttpStatus.OK)
    public ResponseJwtToken googleLogin(HttpServletRequest request) throws JsonProcessingException {
        log.info("google login");
        ResponseJwtToken jwtToken = googleOAuthService.login(request);
        log.info("jwtToken = {}", jwtToken);
        return jwtToken;
    }

    @Operation(summary = "애플 로그인", description = "프론트로부터 Authorization code, idToken을 받아 애플 로그인을 진행합니다.")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "애플 로그인 성공"),
        @ApiResponse(
            responseCode = "400",
            description = "C001 : Authorization header 값이 Bearer 토큰이 아니거나 없습니다.",
            content = @Content(schema = @Schema(hidden = true)))
    })
    @PostMapping(value = "/apple/login")
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

    @Operation(summary = "jwt access token 재발급",
        description = "프론트로부터 jwt refresh token을 받아 jwt access token을 재발급합니다.")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "jwt access token 재발급 성공"),
        @ApiResponse(
            responseCode = "400",
            description = "C001 : Authorization header 값이 JWT 토큰이 아니거나 없습니다.\t\n"
                + "S002 : JWT refresh token이 유효하지 않습니다.\t\n"
                + "S006 : JWT refresh token이 만료되었습니다.",
            content = @Content(schema = @Schema(hidden = true)))
    })
    @GetMapping("/refresh")
    public ResponseJwtToken getAccessToken(HttpServletRequest request) {
        log.info("refresh token");
        String refreshToken = getRefreshToken(request);

        jwtTokenProvider.validRefreshToken(refreshToken);

        String accessToken = jwtTokenProvider.createNewAccessTokenFromRefreshToken(refreshToken);

        return ResponseJwtToken.of(accessToken, "");
    }

    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴 로직 미검증(검증되면 작성하겠습니다)")
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
