package tipitapi.drawmytoday.domain.diary.dto;

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
@Schema(description = "일기 생성 가능 여부 Response")
@AllArgsConstructor
public class GetDiaryLimitResponse {

    @Schema(description = "일기 생성 가능 여부", requiredMode = RequiredMode.REQUIRED)
    private final boolean available;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Schema(description = "마지막 일기 작성 시간", requiredMode = RequiredMode.NOT_REQUIRED)
    private final LocalDateTime lastDiaryCreatedAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Schema(description = "유효 티켓 생성일자", requiredMode = RequiredMode.NOT_REQUIRED)
    private LocalDateTime ticketCreatedAt;

    public static GetDiaryLimitResponse of(boolean available, LocalDateTime lastDiaryCreatedAt,
        LocalDateTime ticketCreatedAt) {
        return new GetDiaryLimitResponse(available, lastDiaryCreatedAt, ticketCreatedAt);
    }
}
