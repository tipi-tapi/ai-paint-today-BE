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

    public void clampContent(int maxLength) {
        substringContent(maxLength);
        if (content.lastIndexOf(".") != content.length() - 1) {
            content = content.substring(0, content.lastIndexOf(".") + 1);
        }
    }

    private void substringContent(int maxLength) {
        if (content.length() > maxLength) {
            content = content.substring(0, maxLength);
        }
    }
}
