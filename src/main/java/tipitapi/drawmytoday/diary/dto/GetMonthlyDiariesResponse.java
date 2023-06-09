package tipitapi.drawmytoday.diary.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.time.LocalDateTime;
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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Schema(description = "일기 날짜", requiredMode = RequiredMode.REQUIRED)
    private final LocalDateTime date;

    public static GetMonthlyDiariesResponse of(Long diaryId, String imageUrl,
        LocalDateTime diaryDate) {
        return new GetMonthlyDiariesResponse(diaryId, imageUrl, diaryDate);
    }
}
