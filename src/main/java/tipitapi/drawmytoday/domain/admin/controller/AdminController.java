package tipitapi.drawmytoday.domain.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tipitapi.drawmytoday.common.resolver.AuthUser;
import tipitapi.drawmytoday.common.response.SuccessResponse;
import tipitapi.drawmytoday.common.security.jwt.JwtTokenInfo;
import tipitapi.drawmytoday.domain.admin.dto.GetDiaryAdminResponse;
import tipitapi.drawmytoday.domain.admin.service.AdminService;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "일기 목록 조회", description = "일기의 프롬프트, 이미지 정보 목록을 반환하는 API")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "일기 목록. Pagination에 대한 세부 항목은 노션의 pageable 필드 설명 문서를 참고해주세요."),
        @ApiResponse(
            responseCode = "403",
            description = "D002 : 접근할 권한이 없습니다.",
            content = @Content(schema = @Schema(hidden = true))),
    })
    @GetMapping("/diaries")
    public ResponseEntity<SuccessResponse<Page<GetDiaryAdminResponse>>> getDiaries(
        @Parameter(name = "size", description = "페이지네이션의 페이지당 데이터 수", in = ParameterIn.QUERY)
        @RequestParam(value = "size", required = false, defaultValue = "20") int size,
        @Parameter(name = "page", description = "페이지네이션의 페이지 넘버. 0부터 시작함", in = ParameterIn.QUERY)
        @RequestParam(value = "page", required = false, defaultValue = "0") int page,
        @Parameter(name = "direction", description = "페이지네이션의 정렬기준. DESC=최신순, ASC=오래된순", in = ParameterIn.QUERY)
        @RequestParam(name = "direction", required = false, defaultValue = "DESC") Direction direction,
        @Parameter(name = "emotion", description = "필터링할 감정 ID", in = ParameterIn.QUERY)
        @RequestParam(name = "emotion", required = false) Long emotionId,
        @Parameter(name = "with_test", description = "테스트용 일기 포함 여부", in = ParameterIn.QUERY)
        @RequestParam(name = "with_test", required = false, defaultValue = "true") boolean withTest,
        @AuthUser @Parameter(hidden = true) JwtTokenInfo tokenInfo
    ) {
        return SuccessResponse.of(
            adminService.getDiaries(tokenInfo.getUserId(), size, page, direction, emotionId,
                withTest)
        ).asHttp(HttpStatus.OK);
    }
}
