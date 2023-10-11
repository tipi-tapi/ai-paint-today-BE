package tipitapi.drawmytoday.domain.diary.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Schema(description = "월별 일기 목록 Response")
public class GetMonthlyDiariesResponse {

    @Schema(description = "일기 아이디", requiredMode = RequiredMode.REQUIRED)
    private final Long id;

    @Schema(description = "대표 이미지 URL", requiredMode = RequiredMode.REQUIRED)
    @Setter
    private String imageUrl;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Schema(description = "일기 날짜", requiredMode = RequiredMode.REQUIRED)
    private final LocalDateTime date;

    @QueryProjection
    public GetMonthlyDiariesResponse(Long id, String imageUrl, LocalDateTime date) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.date = date;
    }

    public static GetMonthlyDiariesResponse of(Long diaryId, String imageUrl,
        LocalDateTime diaryDate) {
        return new GetMonthlyDiariesResponse(diaryId, imageUrl, diaryDate);
    }
}
