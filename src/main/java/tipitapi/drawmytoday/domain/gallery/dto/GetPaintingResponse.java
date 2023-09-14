package tipitapi.drawmytoday.domain.gallery.dto;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.Getter;

@Getter
@Schema(description = "작품 조회 Response")
public class GetPaintingResponse {

    @Schema(description = "작품 제목", requiredMode = RequiredMode.REQUIRED)
    private final String title;

    @Schema(description = "작품 이미지 URL", requiredMode = RequiredMode.REQUIRED)
    private String imageURL;

    @Schema(description = "작품 내용", requiredMode = RequiredMode.NOT_REQUIRED)
    private final String notes;

    @QueryProjection
    public GetPaintingResponse(String title, String imageURL, String notes) {
        this.title = title;
        this.imageURL = imageURL;
        this.notes = notes;
    }

    public void updateImageUrl(String preSignedUrlForShare) {
        this.imageURL = preSignedUrlForShare;
    }
}
