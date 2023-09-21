package tipitapi.drawmytoday.domain.generator.domain.dalle.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateDallEImageRequest {

    private String prompt;
    private int n;
    private String size;
    private String response_format;

    public static CreateDallEImageRequest withBase64(String prompt) {
        return new CreateDallEImageRequest(prompt, 1, "1024x1024", "b64_json");
    }

    public static CreateDallEImageRequest withUrl(String prompt) {
        return new CreateDallEImageRequest(prompt, 1, "512x512", "url");
    }
}
