package tipitapi.drawmytoday.test.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tipitapi.drawmytoday.common.security.jwt.JwtTokenProvider;
import tipitapi.drawmytoday.common.security.jwt.JwtType;
import tipitapi.drawmytoday.common.utils.HeaderUtils;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "토큰 만료", description = "jwt token을 만료시킵니다.",
        security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "jwt token 만료 성공"),
        @ApiResponse(
            responseCode = "400",
            description = "S002 : 유효하지 않은 토큰입니다.",
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(
            responseCode = "404",
            description = "S008 : jwt token이 없습니다.",
            content = @Content(schema = @Schema(hidden = true)))
    })
    @GetMapping("/expire")
    public String getExpiredJwt(HttpServletRequest request) {
        String jwtToken = HeaderUtils.getJwtToken(request, JwtType.BOTH);
        return jwtTokenProvider.expireToken(jwtToken);
    }

    @Operation(summary = "서버 생존 여부 체크용", description = "서버가 살아있는지 체크합니다.")
    @ApiResponse(responseCode = "204", description = "서버 생존")
    @GetMapping("/server")
    public ResponseEntity<Void> verifyServerAlive() {
        return ResponseEntity.noContent().build();
    }
}
