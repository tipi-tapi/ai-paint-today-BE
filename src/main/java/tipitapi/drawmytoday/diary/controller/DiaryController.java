package tipitapi.drawmytoday.diary.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tipitapi.drawmytoday.common.resolver.AuthUser;
import tipitapi.drawmytoday.common.response.SuccessResponse;
import tipitapi.drawmytoday.common.security.jwt.JwtTokenInfo;
import tipitapi.drawmytoday.diary.dto.GetDiaryResponse;
import tipitapi.drawmytoday.diary.dto.GetMonthlyDiariesResponse;
import tipitapi.drawmytoday.diary.service.DiaryService;

@RestController
@RequestMapping("/diary")
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryService diaryService;

    @Operation(summary = "일기 조회", description = "특정 일기의 내용을 반환한다.")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "일기 상세 정보"),
        @ApiResponse(
            responseCode = "403",
            description = "D002 : 자신의 일기에만 접근할 수 있습니다.",
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(
            responseCode = "404",
            description = "D001 : 일기를 찾을 수 없습니다.\t\nI001 : 선택된 이미지를 찾을 수 없습니다.",
            content = @Content(schema = @Schema(hidden = true))),
    })
    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<GetDiaryResponse>> getDiary(
        @Parameter(description = "일기 id", in = ParameterIn.PATH) @PathVariable("id") Long diaryId
        , @AuthUser @Parameter(hidden = true) JwtTokenInfo tokenInfo
    ) {
        return SuccessResponse.of(
            diaryService.getDiary(tokenInfo.getUserId(), diaryId)
        ).asHttp(HttpStatus.OK);
    }

    @Operation(summary = "월별 일기 목록", description = "메인 화면의 캘린더 뷰에서 사용하는, 월별 일기 목록을 반환하는 API")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "입력한 연도의 월에 해당하는 일기 목록이 없으면 배열을 반환하지 않는다."),
        @ApiResponse(
            responseCode = "400",
            description = "C001 : month 값이 1~12 사이의 정수가 아닙니다.",
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(
            responseCode = "404",
            description = "U001: 해당 토큰의 유저를 찾을 수 없습니다.",
            content = @Content(schema = @Schema(hidden = true))),
    })
    @GetMapping("/calendar/monthly")
    public ResponseEntity<SuccessResponse<List<GetMonthlyDiariesResponse>>> getMonthlyDiaries(
        @Parameter(description = "조회할 연도", in = ParameterIn.PATH) @RequestParam("year") int year,
        @Parameter(description = "조회할 달", in = ParameterIn.PATH) @RequestParam("month") int month
        , @AuthUser @Parameter(hidden = true) JwtTokenInfo tokenInfo
    ) {
        return SuccessResponse.of(
            diaryService.getMonthlyDiaries(tokenInfo.getUserId(), year, month)
        ).asHttp(HttpStatus.OK);
    }
}
