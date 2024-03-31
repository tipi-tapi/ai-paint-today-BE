package tipitapi.drawmytoday.domain.diary.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tipitapi.drawmytoday.common.validator.ValidDiaryDate;

@Getter
@Schema(description = "일기 생성 Request")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateDiaryRequest {

    @NotNull
    @Schema(description = "감정 ID")
    private Long emotionId;

    @Schema(description = "일기 키워드(deprecated)", nullable = true)
    private String keyword;

    @Schema(description = "번역된 일기", nullable = true)
    private String translatedNotes;

    @Size(max = 6010)
    @Schema(description = "일기 내용", nullable = true)
    private String notes;

    @NotNull
    @ValidDiaryDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @Schema(description = "일기 날짜")
    private LocalDate diaryDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    @JsonDeserialize(using = LocalTimeDeserializer.class)
    @Schema(description = "현재 유저 시간", nullable = true, example = "12:00:00")
    private LocalTime userTime;

    public LocalTime getUserTime() {
        if (userTime == null) {
            return LocalTime.now();
        }
        return userTime;
    }
}
