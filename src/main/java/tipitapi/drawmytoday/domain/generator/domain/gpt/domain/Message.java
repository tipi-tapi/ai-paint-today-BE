package tipitapi.drawmytoday.domain.generator.domain.gpt.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(force = true)
public class Message {

    private final ChatCompletionsRole role;
    private final String content;

    public Message(ChatCompletionsRole role, String content) {
        this.role = role;
        this.content = content;
    }
}
