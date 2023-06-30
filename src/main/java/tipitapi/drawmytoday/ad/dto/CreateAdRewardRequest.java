package tipitapi.drawmytoday.ad.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tipitapi.drawmytoday.ad.domain.AdType;

@Getter
@Schema(description = "광고 기록 생성 Request")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateAdRewardRequest {

    @Schema(description = "광고 종류", defaultValue = "VIDEO", nullable = true)
    private AdType adType;
}
