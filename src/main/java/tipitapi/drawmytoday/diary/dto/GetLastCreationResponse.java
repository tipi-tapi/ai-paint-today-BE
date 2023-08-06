package tipitapi.drawmytoday.diary.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@Schema(description = "마지막 일기 생성 시각 조회 Response")
@AllArgsConstructor
public class GetLastCreationResponse {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    @Schema(description = "마지막 일기 생성 날짜", nullable = true)
    private ZonedDateTime lastCreation;
}
