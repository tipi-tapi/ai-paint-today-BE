package tipitapi.drawmytoday.domain.generator.domain.karlo.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class KarloUrlResponse {

    private String id;
    private String modelVersion;

    private List<KarloImageUrlResponse> images;

    public String getUrl(int i) {
        return images.get(0).getImageUrl();
    }

    public List<String> getUrls() {
        return images.stream()
            .map(KarloImageUrlResponse::getImageUrl)
            .collect(Collectors.toList());
    }
}
