package tipitapi.drawmytoday.dalle.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateImageRequest {

    private String prompt;
    private int n;
    private String size;
    private String response_format;

    public static CreateImageRequest withBase64(String prompt) {
        return new CreateImageRequest(prompt, 1, "1024x1024", "b64_json");
    }

    public static CreateImageRequest withUrl(String prompt) {
        return new CreateImageRequest(prompt, 1, "1024x1024", "url");
    }
}
