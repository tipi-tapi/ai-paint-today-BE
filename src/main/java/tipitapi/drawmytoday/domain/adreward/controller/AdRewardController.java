package tipitapi.drawmytoday.domain.adreward.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tipitapi.drawmytoday.common.resolver.AuthUser;
import tipitapi.drawmytoday.common.security.jwt.JwtTokenInfo;
import tipitapi.drawmytoday.domain.adreward.service.AdRewardService;

@RestController
@RequestMapping("/ad")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class AdRewardController {

    private final AdRewardService adRewardService;

    @Operation(summary = "(구)광고 기록 생성 (신)티켓 생성", description = "광고 기록 저장 및 티켓 발급을 담당했으나, 현재는 티켓만 발급함")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "성공적으로 티켓을 등록함"),
    })
    @PostMapping()
    public ResponseEntity<Void> createDiary(
        @AuthUser @Parameter(hidden = true) JwtTokenInfo tokenInfo
    ) {
        adRewardService.createTicket(tokenInfo.getUserId());
        return ResponseEntity.noContent().build();
    }
}
