package tipitapi.drawmytoday.diary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Schema(description = "일기 생성 Request")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateDiaryRequest {

    @NotNull
    @Schema(description = "감정 ID")
    private Long emotionId;

    @NotBlank
    @Size(max = 100)
    @Schema(description = "일기 키워드", nullable = true)
    private String keyword;

    @Size(max = 6010)
    @Schema(description = "일기 내용", nullable = true)
    private String notes;
}
