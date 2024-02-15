package tipitapi.drawmytoday.domain.generator.domain.gpt.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * https://platform.openai.com/docs/guides/text-generation/chat-completions-response-format
 */

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GptChatCompletionsResponse {

    private ChoiceResponse[] choices;
    private String id;

}
