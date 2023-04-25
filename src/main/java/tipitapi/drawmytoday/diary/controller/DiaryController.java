package tipitapi.drawmytoday.diary.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import tipitapi.drawmytoday.common.response.SuccessResponse;
import tipitapi.drawmytoday.diary.dto.DiaryResponse;
import tipitapi.drawmytoday.diary.service.DiaryService;

@Controller
@RequestMapping("/diary")
@RequiredArgsConstructor
public class DiaryController {

  private final DiaryService diaryService;

  @Operation(summary = "일기 조회", description = "특정 일기의 내용을 반환한다.")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "일기가 성공적으로 조회됨",
          content = @Content(mediaType = "application/json", schema = @Schema(implementation = DiaryResponse.class))),
  })
  @GetMapping("/{id}")
  public ResponseEntity<SuccessResponse<DiaryResponse>> getDiary(
      @Parameter(description = "가게 id", in = ParameterIn.PATH) @PathVariable("id") Long diaryId) {
    // TODO : Authentication에서 가져오도록, Spring Security 추가 후 수정 필요.
    Long userId = 1L;
    return SuccessResponse.of(
        diaryService.getDiary(userId, diaryId)
    ).asHttp(HttpStatus.OK);
  }
}
