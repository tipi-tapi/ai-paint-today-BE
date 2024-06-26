package tipitapi.drawmytoday.domain.diary.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tipitapi.drawmytoday.common.resolver.AuthUser;
import tipitapi.drawmytoday.common.response.SuccessResponse;
import tipitapi.drawmytoday.common.security.jwt.JwtTokenInfo;
import tipitapi.drawmytoday.domain.diary.dto.*;
import tipitapi.drawmytoday.domain.diary.service.CreateDiaryService;
import tipitapi.drawmytoday.domain.diary.service.DiaryService;
import tipitapi.drawmytoday.domain.generator.exception.ImageGeneratorException;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/diary")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class DiaryController {

    private final DiaryService diaryService;
    private final CreateDiaryService createDiaryService;

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
        @Parameter(description = "일기 id", in = ParameterIn.PATH) @PathVariable("id") Long diaryId,
        @AuthUser @Parameter(hidden = true) JwtTokenInfo tokenInfo
    ) {
        return SuccessResponse.of(
            diaryService.getDiary(tokenInfo.getUserId(), diaryId)
        ).asHttp(HttpStatus.OK);
    }

    @Operation(summary = "월별 일기 목록", description = "메인 화면의 캘린더 뷰에서 사용하는, 월별 일기 목록을 반환하는 API")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "입력한 연도와 월에 해당하는 일기 목록을 반환한다."),
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
        @Parameter(description = "조회할 연도", in = ParameterIn.QUERY) @RequestParam("year") int year,
        @Parameter(description = "조회할 달", in = ParameterIn.QUERY) @RequestParam("month") int month
        , @AuthUser @Parameter(hidden = true) JwtTokenInfo tokenInfo
    ) {
        return SuccessResponse.of(
            diaryService.getMonthlyDiaries(tokenInfo.getUserId(), year, month)
        ).asHttp(HttpStatus.OK);
    }

    @Operation(summary = "특정 날짜 일기 존재 여부 조회", description = "특정 날짜에 일기가 존재하는지 조회하여 반환한다.")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "존재 여부와, 존재할 경우 일기 ID를 반환한다."),
        @ApiResponse(
            responseCode = "400",
            description = "C001 : month 값이 1~12 사이의 정수가 아닙니다.\n"
                + "C001 : day 값이 1~31 사이의 정수가 아닙니다.",
            content = @Content(schema = @Schema(hidden = true))),
    })
    @GetMapping("/calendar/date")
    public ResponseEntity<SuccessResponse<GetDiaryExistByDateResponse>> getDiaryExistByDate(
        @Parameter(description = "조회할 연도", in = ParameterIn.QUERY) @RequestParam("year") int year,
        @Parameter(description = "조회할 달", in = ParameterIn.QUERY) @RequestParam("month") int month,
        @Parameter(description = "조회할 일", in = ParameterIn.QUERY) @RequestParam("day") int day,
        @AuthUser @Parameter(hidden = true) JwtTokenInfo tokenInfo
    ) {
        return SuccessResponse.of(
            diaryService.getDiaryExistByDate(tokenInfo.getUserId(), year, month, day)
        ).asHttp(HttpStatus.OK);
    }

    @Operation(summary = "마지막 일기 생성 시각 조회", description = "유저가 마지막으로 일기를 생성한 시각을 반환한다.")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "마지막 일기 생성 시각 정보"),
    })
    @GetMapping("/last-creation")
    public ResponseEntity<SuccessResponse<GetLastCreationResponse>> getLastCreation(
        @AuthUser @Parameter(hidden = true) JwtTokenInfo tokenInfo
    ) {
        return SuccessResponse.of(
            diaryService.getLastCreation(tokenInfo.getUserId())
        ).asHttp(HttpStatus.OK);
    }

    @Operation(summary = "일기 생성", description = "DALL-E API를 사용하여 이미지를 발급하여 일기를 생성한다.")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "성공적으로 생성된 일기 정보"),
        @ApiResponse(
            responseCode = "400",
            description = "U004 : 이미 그림일기를 그린 유저입니다.",
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(
            responseCode = "404",
            description = "E001 : 감정을 찾을 수 없습니다.",
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(
            responseCode = "409",
            description = "D001 : 이미 일기를 그린 날짜입니다.",
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(
            responseCode = "404",
            description = "T001 : 유효한 티켓이 존재하지 않습니다.",
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(
            responseCode = "500",
            description = "DE001 : DALL-E 이미지 생성에 실패하였습니다.",
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(
            responseCode = "500",
            description = "IIS001 : 이미지 스트림을 가져오는데 실패하였습니다.",
            content = @Content(schema = @Schema(hidden = true))),
    })
    @PostMapping()
    public ResponseEntity<SuccessResponse<CreateDiaryResponse>> createDiary(
        @RequestBody @Valid CreateDiaryRequest createDiaryRequest,
        @AuthUser @Parameter(hidden = true) JwtTokenInfo tokenInfo
    ) throws ImageGeneratorException {
        return SuccessResponse.of(
            createDiaryService.createDiary(tokenInfo.getUserId(), createDiaryRequest)
        ).asHttp(HttpStatus.CREATED);
    }

    @Operation(summary = "일기 수정", description = "주어진 일기의 내용을 수정한다.")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "성공적으로 일기 내용을 수정함"),
        @ApiResponse(
            responseCode = "403",
            description = "D002 : 자신의 일기에만 접근할 수 있습니다.",
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(
            responseCode = "404",
            description = "D001 : 일기를 찾을 수 없습니다.",
            content = @Content(schema = @Schema(hidden = true))),
    })
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateDiaryNotes(
        @RequestBody @Valid UpdateDiaryRequest updateDiaryRequest,
        @Parameter(description = "일기 id", in = ParameterIn.PATH) @PathVariable("id") Long diaryId,
        @AuthUser @Parameter(hidden = true) JwtTokenInfo tokenInfo
    ) {
        diaryService.updateDiaryNotes(tokenInfo.getUserId(), diaryId,
            updateDiaryRequest.getNotes());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "일기 삭제", description = "주어진 ID의 일기를 삭제(Soft Delete)한다.")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "성공적으로 일기 내용을 삭제함"),
        @ApiResponse(
            responseCode = "403",
            description = "D002 : 자신의 일기에만 접근할 수 있습니다.",
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(
            responseCode = "404",
            description = "D001 : 일기를 찾을 수 없습니다.",
            content = @Content(schema = @Schema(hidden = true))),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiary(
        @AuthUser JwtTokenInfo tokenInfo,
        @Parameter(description = "일기 id", in = ParameterIn.PATH) @PathVariable("id") Long diaryId
    ) {
        diaryService.deleteDiary(tokenInfo.getUserId(), diaryId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "일기 생성 가능 여부 조회", description = "유저가 일기를 생성할 수 있는 티켓이 있는지 여부를 반환한다.")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "일기 생성 가능 정보"),
    })
    @GetMapping("/limit")
    public ResponseEntity<SuccessResponse<GetDiaryLimitResponse>> getDrawLimit(
        @AuthUser @Parameter(hidden = true) JwtTokenInfo tokenInfo
    ) {
        return SuccessResponse.of(
            diaryService.getDrawLimit(tokenInfo.getUserId())
        ).asHttp(HttpStatus.OK);
    }

    @Operation(summary = "일기 이미지 재생성", description = "주어진 ID에 해당하는 일기를 기반으로 이미지를 재생성한다.")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "일기 이미지 재생성 성공"),
        @ApiResponse(
            responseCode = "404",
            description = "T001 : 유효한 티켓이 존재하지 않습니다.",
            content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(
            responseCode = "500",
            description = "DE001 : DALL-E 이미지 생성에 실패하였습니다.\t\nIIS001 : 이미지 스트림을 가져오는데 실패하였습니다."
                + "\t\nP001 : 기존에 그린 일기 이미지에 해당하는 프롬프트가 존재하지 않습니다.",
            content = @Content(schema = @Schema(hidden = true))),
    })
    @PostMapping("/{id}/regenerate")
    public ResponseEntity<Void> regenerateDiaryImage(
        @AuthUser JwtTokenInfo tokenInfo,
        @Parameter(description = "일기 id", in = ParameterIn.PATH) @PathVariable("id") Long diaryId,
        @RequestBody(required = false) RegenerateDiaryRequest request
    ) throws ImageGeneratorException {
        String diary = request == null ? "" : request.getDiary();
        createDiaryService.regenerateDiaryImage(tokenInfo.getUserId(), diaryId, diary);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
