package tipitapi.drawmytoday.diary.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@Schema(description = "월별 일기 목록 Response")
@AllArgsConstructor
public class GetMonthlyDiariesResponse {

    @Schema(description = "일기 아이디", requiredMode = RequiredMode.REQUIRED)
    private final Long id;

    @Schema(description = "이미지 URL", requiredMode = RequiredMode.REQUIRED)
    private final String imageUrl;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    @Schema(description = "일기 날짜", requiredMode = RequiredMode.REQUIRED)
    private final ZonedDateTime date;

    public static GetMonthlyDiariesResponse of(Long diaryId, String imageUrl,
        ZonedDateTime diaryDate) {
        return new GetMonthlyDiariesResponse(diaryId, imageUrl, diaryDate);
    }
}
