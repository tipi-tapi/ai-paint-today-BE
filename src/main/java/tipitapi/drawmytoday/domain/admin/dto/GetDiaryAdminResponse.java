package tipitapi.drawmytoday.domain.admin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@Getter
@Schema(description = "관리자용 일기 목록의 일기 정보 Response")
public class GetDiaryAdminResponse {

    @Schema(description = "일기 ID", requiredMode = RequiredMode.REQUIRED)
    private final Long id;

    @Schema(description = "일기 프롬프트", requiredMode = RequiredMode.NOT_REQUIRED)
    private final String prompt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Schema(description = "일기 작성 시간", requiredMode = RequiredMode.REQUIRED)
    private final LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Schema(description = "이미지 생성 시간", requiredMode = RequiredMode.REQUIRED)
    private final LocalDateTime imageCreatedAt;

    @Schema(description = "일기 이미지 URL", requiredMode = RequiredMode.NOT_REQUIRED)
    private String imageURL;

    @Schema(description = "평가 점수 (1~5 사이의 숫자)")
    private final String review;

    @Schema(description = "테스트 일기 여부", requiredMode = RequiredMode.REQUIRED)
    private final boolean isTest;

    @QueryProjection
    public GetDiaryAdminResponse(Long id, String imageURL, String prompt,
        LocalDateTime createdAt, LocalDateTime imageCreatedAt, String review, boolean isTest) {
        this.id = id;
        this.imageURL = imageURL;
        this.prompt = prompt;
        this.createdAt = createdAt;
        this.imageCreatedAt = imageCreatedAt;
        this.review = review;
        this.isTest = isTest;
    }

    public void updateImageUrl(String imageUrl) {
        this.imageURL = imageUrl;
    }

    public static class Page extends PageImpl<GetDiaryAdminResponse> {

        public Page(List<GetDiaryAdminResponse> content,
            Pageable pageable, long total) {
            super(content, pageable, total);
        }
    }
}
