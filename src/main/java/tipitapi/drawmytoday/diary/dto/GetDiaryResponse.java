package tipitapi.drawmytoday.diary.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import tipitapi.drawmytoday.diary.domain.Diary;

@Getter
@Schema(description = "일기 상세 Response")
@AllArgsConstructor
public class GetDiaryResponse {

    @Schema(description = "일기 아이디", requiredMode = RequiredMode.REQUIRED)
    private final Long id;

    @Schema(description = "이미지 URL", requiredMode = RequiredMode.REQUIRED)
    private final String imageUrl;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    @Schema(description = "일기 날짜", requiredMode = RequiredMode.REQUIRED)
    private final ZonedDateTime date;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    @Schema(description = "일기 작성 시간", requiredMode = RequiredMode.REQUIRED)
    private final ZonedDateTime createdAt;

    @Schema(description = "감정", requiredMode = RequiredMode.REQUIRED)
    private final String emotion;

    @Schema(description = "일기 내용", requiredMode = RequiredMode.NOT_REQUIRED)
    private String notes;

    @Schema(description = "프롬프트", requiredMode = RequiredMode.NOT_REQUIRED)
    private String prompt;

    public static GetDiaryResponse of(Diary diary, String imageUrl, String emotionText,
        String promptText) {
        return new GetDiaryResponse(diary.getDiaryId(), imageUrl, diary.getDiaryDateWithZone(),
            diary.getCreatedAtWithZone(), emotionText, diary.getNotes(), promptText);
    }
}
