package tipitapi.drawmytoday.domain.diary.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tipitapi.drawmytoday.common.resolver.AuthUser;
import tipitapi.drawmytoday.common.response.SuccessResponse;
import tipitapi.drawmytoday.common.security.jwt.JwtTokenInfo;
import tipitapi.drawmytoday.domain.diary.dto.CreateDiaryResponse;
import tipitapi.drawmytoday.domain.diary.dto.CreateTestDiaryRequest;
import tipitapi.drawmytoday.domain.diary.service.CreateDiaryService;
import tipitapi.drawmytoday.domain.generator.exception.ImageGeneratorException;

@Profile("!prod")
@RestController
@RequiredArgsConstructor
@RequestMapping("/diary")
@SecurityRequirement(name = "Bearer Authentication")
public class TestDiaryController {

    private final CreateDiaryService createDiaryService;

    @Operation(summary = "테스트 일기 생성", description = "테스트 일기를 생성합니다.(티켓 소모 x)")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "테스트 일기 생성 성공"),
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
            responseCode = "500",
            description = "DE001 : DALL-E 이미지 생성에 실패하였습니다. \r\n "
                + "IIS001 : 이미지 스트림을 가져오는데 실패하였습니다.",
            content = @Content(schema = @Schema(hidden = true))),
    })
    @PostMapping("/test")
    public ResponseEntity<SuccessResponse<CreateDiaryResponse>> createTestDiary(
        @RequestBody @Valid CreateTestDiaryRequest createTestDiaryRequest,
        @AuthUser @Parameter(hidden = true) JwtTokenInfo tokenInfo) throws ImageGeneratorException {
        return SuccessResponse.of(createDiaryService.createTestDiary(
            tokenInfo.getUserId(), createTestDiaryRequest
        )).asHttp(HttpStatus.CREATED);
    }

}
