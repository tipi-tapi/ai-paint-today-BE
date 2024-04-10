package tipitapi.drawmytoday.domain.diary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Schema(description = "일기 리뷰 Request")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewDiaryRequest {

    @NotNull
    @Schema(description = "평가 점수 (1~5 사이의 숫자)")
    private String review;

    public String getReview() {
        try {
            int review = Integer.parseInt(this.review);
            if (review > 5) {
                return "5";
            } else if (review < 1) {
                return "1";
            } else {
                return this.review;
            }
        } catch (NumberFormatException e) {
            String errorMessage = String.format("평가 점수는 숫자여야 합니다. input: %s", this.review);
            throw new IllegalArgumentException(errorMessage);
        }
    }
}
