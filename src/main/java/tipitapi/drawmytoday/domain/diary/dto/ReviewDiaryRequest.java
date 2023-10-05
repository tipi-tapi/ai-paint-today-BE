package tipitapi.drawmytoday.domain.diary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Schema(description = "일기 리뷰 Request")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewDiaryRequest {

    @NotNull
    @Min(value = 1, message = "review 값은 1~5 사이의 값이어야 합니다.")
    @Max(value = 5, message = "review 값은 1~5 사이의 값이어야 합니다.")
    @Schema(description = "평가 점수 (1~5 사이의 숫자)")
    private String review;
}
