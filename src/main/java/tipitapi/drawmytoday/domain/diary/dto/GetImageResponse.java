package tipitapi.drawmytoday.domain.diary.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@Schema(description = "이미지 Response")
@JsonInclude(Include.NON_NULL)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GetImageResponse {

    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Schema(description = "이미지 생성 시각", requiredMode = RequiredMode.REQUIRED)
    private LocalDateTime createdAt;

    @Schema(description = "대표 이미지 여부", requiredMode = RequiredMode.REQUIRED)
    private boolean selected;

    @Schema(description = "이미지 URL", requiredMode = RequiredMode.REQUIRED)
    private String url;

    public static GetImageResponse of
        (Long id, LocalDateTime createdAt, boolean selected, String url) {
        return new GetImageResponse(id, createdAt, selected, url);
    }
}
