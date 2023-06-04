package tipitapi.drawmytoday.oauth.controller;

import static tipitapi.drawmytoday.common.exception.ErrorCode.JWT_REFRESH_TOKEN_NOT_FOUND;

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
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tipitapi.drawmytoday.common.resolver.AuthUser;
import tipitapi.drawmytoday.common.security.jwt.JwtProperties;
import tipitapi.drawmytoday.common.security.jwt.JwtTokenInfo;
import tipitapi.drawmytoday.common.security.jwt.JwtTokenProvider;
import tipitapi.drawmytoday.common.security.jwt.exception.InvalidTokenException;
import tipitapi.drawmytoday.common.security.jwt.exception.TokenNotFoundException;
import tipitapi.drawmytoday.oauth.dto.RequestAppleLogin;
import tipitapi.drawmytoday.oauth.dto.ResponseAccessToken;
import tipitapi.drawmytoday.oauth.dto.ResponseJwtToken;
import tipitapi.drawmytoday.oauth.service.AppleOAuthService;
import tipitapi.drawmytoday.oauth.service.GoogleOAuthService;
import tipitapi.drawmytoday.oauth.service.OAuthService;

@RestController
@RequestMapping("/oauth2")
@RequiredArgsConstructor
public class AuthController {

    private final OAuthService oAuthService;
    private final GoogleOAuthService googleOAuthService;
    private final AppleOAuthService appleOAuthService;
    private final JwtTokenProvider jwtTokenProvider;


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
        return googleOAuthService.login(request);
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
        return appleOAuthService.login(request, requestAppleLogin);
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
    public ResponseAccessToken getAccessToken(HttpServletRequest request) {
        String refreshToken = getRefreshToken(request);

        jwtTokenProvider.validRefreshToken(refreshToken);

        String accessToken = jwtTokenProvider.createNewAccessTokenFromRefreshToken(refreshToken);

        return ResponseAccessToken.of(accessToken);
    }

    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴 로직 미검증(검증되면 작성하겠습니다)")
    @DeleteMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    public void deleteAccount(@AuthUser JwtTokenInfo tokenInfo) {
        oAuthService.deleteAccount(tokenInfo.getUserId());

    }

    private String getRefreshToken(HttpServletRequest request) {
        String authorization = request.getHeader(JwtProperties.REFRESH_TOKEN_HEADER);

        if (Objects.isNull(authorization)) {
            throw new TokenNotFoundException(JWT_REFRESH_TOKEN_NOT_FOUND);
        }

        String[] tokens = StringUtils.delimitedListToStringArray(authorization, " ");

        if (tokens.length != 2 || !"Bearer".equals(tokens[0])) {
            throw new InvalidTokenException();
        }

        return tokens[1];
    }

}
