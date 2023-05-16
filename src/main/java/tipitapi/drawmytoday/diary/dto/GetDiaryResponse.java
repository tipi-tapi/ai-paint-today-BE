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
import tipitapi.drawmytoday.diary.domain.Diary;
import tipitapi.drawmytoday.diary.domain.Image;
import tipitapi.drawmytoday.emotion.domain.Emotion;

@Getter
@Schema(description = "일기 상세 Response")
@AllArgsConstructor
public class GetDiaryResponse {

    @Schema(description = "일기 아이디", requiredMode = RequiredMode.REQUIRED)
    private final Long id;

    @Schema(description = "이미지 URL", requiredMode = RequiredMode.REQUIRED)
    private final String imageUrl;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Schema(description = "일기 날짜", requiredMode = RequiredMode.REQUIRED)
    private final LocalDateTime date;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Schema(description = "일기 작성 시간", requiredMode = RequiredMode.REQUIRED)
    private final LocalDateTime createdAt;

    @Schema(description = "감정", requiredMode = RequiredMode.REQUIRED)
    private final String emotion;

    @Schema(description = "일기 내용", requiredMode = RequiredMode.NOT_REQUIRED)
    private String notes;

    public static GetDiaryResponse of(Diary diary, Image image, Emotion emotion) {
        return new GetDiaryResponse(diary.getDiaryId(), image.getImageUrl(), diary.getDiaryDate(),
            diary.getCreatedAt(), emotion.getName(), diary.getNotes());
    }
}
