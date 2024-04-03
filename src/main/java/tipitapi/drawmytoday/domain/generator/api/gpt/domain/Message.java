package tipitapi.drawmytoday.domain.generator.api.gpt.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import tipitapi.drawmytoday.domain.generator.domain.TextGeneratorContent;

@Getter
@NoArgsConstructor(force = true)
public class Message implements TextGeneratorContent {

    private final ChatCompletionsRole role;
    private String content;

    public Message(ChatCompletionsRole role, String content) {
        this.role = role;
        this.content = content;
    }
}
