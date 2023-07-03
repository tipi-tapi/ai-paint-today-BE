package tipitapi.drawmytoday.oauth.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tipitapi.drawmytoday.common.resolver.AuthUser;
import tipitapi.drawmytoday.common.security.jwt.JwtTokenInfo;
import tipitapi.drawmytoday.common.security.jwt.JwtTokenProvider;
import tipitapi.drawmytoday.common.security.jwt.JwtType;
import tipitapi.drawmytoday.common.utils.HeaderUtils;
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
            description = "C001 : 토큰 형식이 Bearer 형식이 아닙니다.",
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(
            responseCode = "404",
            description = "S007 : Authorization header에 토큰이 비었습니다.",
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(
            responseCode = "500",
            description = "O002 : 구글 OAuth 서버와의 통신에 실패했습니다.",
            content = @Content(schema = @Schema(hidden = true)))
    })
    @PostMapping(value = "/google/login")
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
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(
            responseCode = "404",
            description = "S007 : Authorization header에 토큰이 비었습니다.",
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(
            responseCode = "500",
            description = "O002 : 애플 OAuth 서버와의 통신에 실패했습니다.",
            content = @Content(schema = @Schema(hidden = true)))
    })
    @PostMapping(value = "/apple/login")
    public ResponseJwtToken appleLogin(HttpServletRequest request,
        @RequestBody @Valid RequestAppleLogin requestAppleLogin)
        throws IOException {
        return appleOAuthService.login(request, requestAppleLogin);
    }

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
        String refreshToken = HeaderUtils.getJwtToken(request, JwtType.REFRESH);
        jwtTokenProvider.validRefreshToken(refreshToken);
        String accessToken = jwtTokenProvider.createNewAccessTokenFromRefreshToken(refreshToken);
        return ResponseAccessToken.of(accessToken);
    }

    @Operation(summary = "회원 탈퇴", description = "소셜로그인 탈퇴를 진행하고, 회원을 deleted 상태로 변경합니다.",
        security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "소셜로그인 탈퇴 및 회원 삭제 성공"),
        @ApiResponse(
            responseCode = "404",
            description = "U001 : JwtToken의 userId에 해당하는 유저가 존재하지 않습니다.\t\n" +
                "O001 : JwtToken의 userId에 해당하는 유저의 refreshtoken이 존재하지 않습니다.",
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(
            responseCode = "500",
            description = "C004 : 소셜서버와의 통신을 실패했습니다.",
            content = @Content(schema = @Schema(hidden = true)))
    })
    @DeleteMapping("/users")
    public ResponseEntity<Void> deleteAccount(@AuthUser JwtTokenInfo tokenInfo) {
        oAuthService.deleteAccount(tokenInfo.getUserId());
        return ResponseEntity.noContent().build();
    }
}
