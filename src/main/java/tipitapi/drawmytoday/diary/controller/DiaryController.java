package tipitapi.drawmytoday.diary.controller;

import static tipitapi.drawmytoday.common.util.ParseUserId.parseUserId;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
  public ResponseEntity<SuccessResponse<DiaryResponse>> getDiary(Authentication authentication,
      @PathVariable("id") Long diaryId) {
    Long userId = parseUserId(authentication);
    return SuccessResponse.of(
        diaryService.getDiary(userId, diaryId)
    ).asHttp(HttpStatus.OK);
  }
}
