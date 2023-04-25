package tipitapi.drawmytoday.diary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@Schema(description = "일기 상세 Response")
@AllArgsConstructor
public class DiaryResponse {
  @Schema(description = "일기 아이디")
  private final Long id;

  @Schema(description = "이미지 URL")
  private final String imageUrl;

  @Schema(description = "일기 날짜")
  private final LocalDateTime date;

  @Schema(description = "일기 작성 시간")
  private final LocalDateTime createdAt;

  @Schema(description = "일기 내용")
  private String notes;

  @Schema(description = "감정 목록")
  private final List<String> emotions;
}
