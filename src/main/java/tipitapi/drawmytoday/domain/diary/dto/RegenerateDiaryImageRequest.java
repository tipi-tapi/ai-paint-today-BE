package tipitapi.drawmytoday.domain.diary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Schema(description = "일기 이미지 재생성 Request")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RegenerateDiaryImageRequest {

    @Schema(description = "일기 키워드", nullable = true)
    private String keyword;

}
