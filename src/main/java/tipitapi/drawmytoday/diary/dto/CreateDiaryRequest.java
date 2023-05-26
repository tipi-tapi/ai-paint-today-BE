package tipitapi.drawmytoday.diary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Schema(description = "일기 생성 Request")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CreateDiaryRequest {

    @Schema(description = "감정 ID")
    private Long emotionId;

    // TODO : 문자열 길이 제한 및 validation 추가 필요
    @Schema(description = "일기 키워드")
    private String keyword;

    // TODO : 문자열 길이 제한 및 validation 추가 필요
    @Schema(description = "일기 내용", nullable = true)
    private String notes;
}
