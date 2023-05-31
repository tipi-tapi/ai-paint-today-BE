package tipitapi.drawmytoday.dalle.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateImageRequest {

    private String prompt;
    private int n;
    private String size;
    private String response_format;

    public static CreateImageRequest of(String prompt) {
        return new CreateImageRequest(prompt, 1, "1024x1024", "b64_json");
    }
}
