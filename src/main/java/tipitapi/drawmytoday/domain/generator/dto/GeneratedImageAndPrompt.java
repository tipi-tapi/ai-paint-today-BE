package tipitapi.drawmytoday.domain.generator.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GeneratedImageAndPrompt {

    private final String prompt;
    private final byte[] image;
}
