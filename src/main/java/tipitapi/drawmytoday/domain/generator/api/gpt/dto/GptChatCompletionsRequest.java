package tipitapi.drawmytoday.domain.generator.api.gpt.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import tipitapi.drawmytoday.domain.generator.api.gpt.domain.ChatCompletionsRole;
import tipitapi.drawmytoday.domain.generator.api.gpt.domain.Message;

/**
 * https://platform.openai.com/docs/guides/text-generation/chat-completions-api
 */

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GptChatCompletionsRequest {

    private final String model;
    private final List<Message> messages;
    private final int maxTokens = 100;
    private final float temperature = 0;

    public GptChatCompletionsRequest() {
        this.model = "gpt-3.5-turbo";
        this.messages = new ArrayList<>();
    }

    private GptChatCompletionsRequest(String gptChatCompletionsPrompt) {
        this.model = "gpt-3.5-turbo";
        List<Message> messages = new ArrayList<>();
        messages.add(new Message(ChatCompletionsRole.system, gptChatCompletionsPrompt));
        this.messages = messages;
    }

    public static GptChatCompletionsRequest createFirstMessage(String gptChatCompletionsPrompt,
        String diaryNote) {
        GptChatCompletionsRequest request = new GptChatCompletionsRequest(gptChatCompletionsPrompt);
        request.addUserMessage(diaryNote);
        return request;
    }

    public static GptChatCompletionsRequest createRegenerateMessage(
        List<Message> previousGptMessages, String gptRegeneratePrompt) {
        GptChatCompletionsRequest request = new GptChatCompletionsRequest();
        request.addPreviousGptMessages(previousGptMessages);
        request.addUserMessage(gptRegeneratePrompt);
        return request;
    }

    private void addUserMessage(String userMessage) {
        this.messages.add(new Message(ChatCompletionsRole.user, userMessage));
    }

    private void addPreviousGptMessages(List<Message> previousGptMessages) {
        this.messages.addAll(previousGptMessages);
    }
}
