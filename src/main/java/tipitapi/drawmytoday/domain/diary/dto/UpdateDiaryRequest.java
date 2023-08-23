package tipitapi.drawmytoday.domain.diary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Schema(description = "일기 수정 Request")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateDiaryRequest {

    @Size(max = 6010)
    @Schema(description = "일기 내용. null로 요청할 경우, 일기의 내용을 null로 변경한다.", nullable = true)
    private String notes;
}
