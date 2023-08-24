package tipitapi.drawmytoday.dalle.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GeneratedImageAndPrompt {

    private final String prompt;
    private final byte[] image;
}
