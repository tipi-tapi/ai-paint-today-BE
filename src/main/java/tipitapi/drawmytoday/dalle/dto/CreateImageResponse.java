package tipitapi.drawmytoday.dalle.dto;

import java.util.List;
import lombok.Getter;

@Getter
public class CreateImageResponse {

    private List<DallEBase64> data;

    @Getter
    public static class DallEBase64 {

        private String b64_json;
    }
}
