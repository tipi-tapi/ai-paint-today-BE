package tipitapi.drawmytoday.diary.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Schema(description = "일기 생성 Request")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateDiaryRequest {

    @NotNull
    @Schema(description = "감정 ID")
    private Long emotionId;

    @Size(max = 100)
    @Schema(description = "일기 키워드", nullable = true)
    private String keyword;

    @Size(max = 6010)
    @Schema(description = "일기 내용", nullable = true)
    private String notes;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @Schema(description = "일기 날짜")
    private LocalDate diaryDate;
}
