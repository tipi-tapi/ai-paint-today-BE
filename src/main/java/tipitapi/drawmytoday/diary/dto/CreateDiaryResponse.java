package tipitapi.drawmytoday.diary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Schema(description = "일기 생성 Response")
@RequiredArgsConstructor
public class CreateDiaryResponse {

    @Schema(description = "일기 ID")
    private final Long id;
}
