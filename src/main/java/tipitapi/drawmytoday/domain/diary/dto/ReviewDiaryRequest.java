package tipitapi.drawmytoday.domain.diary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tipitapi.drawmytoday.domain.diary.domain.ReviewType;

@Getter
@Schema(description = "일기 리뷰 Request")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewDiaryRequest {

    @NotNull
    @Schema(description = "평가 정보")
    private ReviewType review;
}
