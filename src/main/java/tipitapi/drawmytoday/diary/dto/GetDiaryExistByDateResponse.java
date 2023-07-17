package tipitapi.drawmytoday.diary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@Schema(description = "특정 날짜 일기 존재 여부 조회 Response")
@AllArgsConstructor
public class GetDiaryExistByDateResponse {

    @Schema(description = "일기 존재 여부", requiredMode = RequiredMode.REQUIRED)
    private final boolean exist;

    @Schema(description = "일기 아이디", requiredMode = RequiredMode.NOT_REQUIRED)
    private final Long diaryId;

    public static GetDiaryExistByDateResponse ofExist(Long diaryId) {
        return new GetDiaryExistByDateResponse(true, diaryId);
    }

    public static GetDiaryExistByDateResponse ofNotExist() {
        return new GetDiaryExistByDateResponse(false, null);
    }
}
