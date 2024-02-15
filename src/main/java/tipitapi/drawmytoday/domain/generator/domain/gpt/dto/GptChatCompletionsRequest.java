package tipitapi.drawmytoday.domain.generator.domain.gpt.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import tipitapi.drawmytoday.domain.generator.domain.gpt.domain.ChatCompletionsRole;
import tipitapi.drawmytoday.domain.generator.domain.gpt.domain.Message;

/**
 * https://platform.openai.com/docs/guides/text-generation/chat-completions-api
 */

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GptChatCompletionsRequest {

    private final String model;
    private final List<Message> messages;

    private GptChatCompletionsRequest(String gptChatCompletionsPrompt) {
        this.model = "gpt-3.5-turbo";
        List<Message> messages = new ArrayList<>();
        messages.add(new Message(ChatCompletionsRole.SYSTEM, gptChatCompletionsPrompt));
        this.messages = messages;
    }

    public static GptChatCompletionsRequest createFirstMessage(String gptChatCompletionsPrompt,
        String diaryNote) {
        GptChatCompletionsRequest request = new GptChatCompletionsRequest(gptChatCompletionsPrompt);
        request.addUserMessage(diaryNote);
        return request;
    }

    private void addUserMessage(String userMessage) {
        this.messages.add(new Message(ChatCompletionsRole.USER, userMessage));
    }
}
