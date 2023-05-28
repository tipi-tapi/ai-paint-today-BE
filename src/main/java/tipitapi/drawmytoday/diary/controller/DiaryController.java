package tipitapi.drawmytoday.diary.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tipitapi.drawmytoday.common.resolver.AuthUser;
import tipitapi.drawmytoday.common.response.SuccessResponse;
import tipitapi.drawmytoday.common.security.jwt.JwtTokenInfo;
import tipitapi.drawmytoday.diary.dto.CreateDiaryRequest;
import tipitapi.drawmytoday.diary.dto.CreateDiaryResponse;
import tipitapi.drawmytoday.diary.dto.GetDiaryResponse;
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

    @Operation(summary = "일기 생성", description = "dall-e API를 사용하여 이미지를 발급하여 일기를 생성한다.")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "성공적으로 생성된 일기 정보")
    })
    @PostMapping()
    public ResponseEntity<SuccessResponse<CreateDiaryResponse>> createDiary(
        @RequestBody @Valid CreateDiaryRequest createDiaryRequest,
        @AuthUser @Parameter(hidden = true) JwtTokenInfo tokenInfo
    ) throws Exception {
        // TODO : Swagger Error 명세 필요
        return SuccessResponse.of(
            diaryService.createDiary(tokenInfo.getUserId(), createDiaryRequest.getEmotionId(),
                createDiaryRequest.getKeyword(), createDiaryRequest.getNotes())
        ).asHttp(HttpStatus.CREATED);
    }
}
