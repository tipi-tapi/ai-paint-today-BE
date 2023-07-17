package tipitapi.drawmytoday.admin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@Getter
@Schema(description = "관리자용 일기 목록의 일기 정보 Response")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class GetDiaryAdminResponse {

    @Schema(description = "일기 ID", requiredMode = RequiredMode.REQUIRED)
    private final Long id;

    @Schema(description = "일기 이미지 URL", requiredMode = RequiredMode.NOT_REQUIRED)
    private final String imageURL;

    @Schema(description = "일기 프롬프트", requiredMode = RequiredMode.NOT_REQUIRED)
    private final String prompt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Schema(description = "일기 작성 시간", requiredMode = RequiredMode.REQUIRED)
    private final LocalDateTime createdAt;

    public static GetDiaryAdminResponse of(Long id, String imageURL, String prompt,
        LocalDateTime createdAt) {
        return new GetDiaryAdminResponse(id, imageURL, prompt, createdAt);
    }

    public static class Page extends PageImpl<GetDiaryAdminResponse> {

        public Page(List<GetDiaryAdminResponse> content,
            Pageable pageable, long total) {
            super(content, pageable, total);
        }
    }
}
