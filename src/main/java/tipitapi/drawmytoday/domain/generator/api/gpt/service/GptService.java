package tipitapi.drawmytoday.domain.generator.api.gpt.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import tipitapi.drawmytoday.domain.diary.domain.Prompt;
import tipitapi.drawmytoday.domain.generator.api.gpt.domain.Message;
import tipitapi.drawmytoday.domain.generator.api.gpt.dto.GptChatCompletionsRequest;
import tipitapi.drawmytoday.domain.generator.api.gpt.dto.GptChatCompletionsResponse;
import tipitapi.drawmytoday.domain.generator.api.gpt.exception.GptRequestFailException;
import tipitapi.drawmytoday.domain.generator.exception.TextGeneratorException;
import tipitapi.drawmytoday.domain.generator.service.TextGeneratorService;

@Slf4j
@Service
@Transactional(readOnly = true)
public class GptService implements TextGeneratorService {

    private final RestTemplate openaiRestTemplate;
    private final String chatCompletionsUrl;
    private final String gptChatCompletionsPrompt;
    private final String gptRegeneratePrompt;
    private final ObjectMapper objectMapper;

    public GptService(RestTemplate openaiRestTemplate,
        @Value("${openai.gpt.chat_completions_prompt}") String gptChatCompletionsPrompt,
        @Value("${openai.gpt.chat_completions_regenerate_prompt}") String gptRegeneratePrompt,
        ObjectMapper objectMapper) {
        this.openaiRestTemplate = openaiRestTemplate;
        this.chatCompletionsUrl = "https://api.openai.com/v1/chat/completions";
        this.gptChatCompletionsPrompt = gptChatCompletionsPrompt;
        this.gptRegeneratePrompt = gptRegeneratePrompt;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional(noRollbackFor = TextGeneratorException.class)
    public List<Message> generatePrompt(String diaryNote, int maxLength) {
        Assert.hasText(diaryNote, "일기 내용이 없습니다.");

        GptChatCompletionsRequest request = GptChatCompletionsRequest.createFirstMessage(
            gptChatCompletionsPrompt, diaryNote);
        HttpEntity<GptChatCompletionsRequest> httpEntity = createChatCompletionsRequest(request);
        return requestGptChatCompletion(httpEntity, maxLength);
    }

    @Override
    @Transactional(noRollbackFor = TextGeneratorException.class)
    public List<Message> regeneratePrompt(String diaryNote, Prompt prompt, int maxLength) {
        Assert.hasText(diaryNote, "일기 내용이 없습니다.");

        String gptContent = prompt.getPromptGeneratorResult().getPromptGeneratorContent();
        List<Message> previousGptMessages = parsingGptContent(gptContent);
        String gptRegeneratePrompt = diaryNote + "\n\n" + this.gptRegeneratePrompt;
        GptChatCompletionsRequest newGptChatCompletionsRequest = GptChatCompletionsRequest.createRegenerateMessage(
            previousGptMessages, gptRegeneratePrompt);
        HttpEntity<GptChatCompletionsRequest> httpEntity = createChatCompletionsRequest(
            newGptChatCompletionsRequest);
        return requestGptChatCompletion(httpEntity, maxLength);
    }

    private List<Message> parsingGptContent(String gptContent) {
        try {
            return objectMapper.readValue(gptContent, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            log.warn("GPT 결과를 파싱하는데 실패했습니다. message: {}", e.getMessage());
            throw new IllegalArgumentException(e);
        }
    }

    private List<Message> requestGptChatCompletion(
        HttpEntity<GptChatCompletionsRequest> httpEntity, int maxLength) {
        ResponseEntity<GptChatCompletionsResponse> responseEntity = null;
        try {
            responseEntity = openaiRestTemplate.postForEntity(
                chatCompletionsUrl, httpEntity, GptChatCompletionsResponse.class);

            validIsSuccessfulRequest(responseEntity);
            Message responseMessage = responseEntity.getBody().getChoices()[0].getMessage();
            responseMessage.clampContent(maxLength);
            List<Message> messages = httpEntity.getBody().getMessages();
            messages.add(responseMessage);
            return messages;
        } catch (RestClientException e) {
            log.warn("GPT chat completions 요청에 실패했습니다. message: {}", e.getMessage());
            throw new GptRequestFailException(e);
        }
    }

    private static void validIsSuccessfulRequest(
        ResponseEntity<GptChatCompletionsResponse> responseEntity) {
        if (!responseEntity.getStatusCode().is2xxSuccessful()
            || responseEntity.getBody() == null
            || responseEntity.getBody().getChoices() == null
            || responseEntity.getBody().getChoices().length == 0
            || responseEntity.getBody().getChoices()[0].getMessage() == null
            || responseEntity.getBody().getChoices()[0].getMessage().getContent() == null
            || responseEntity.getBody().getChoices()[0].getMessage().getContent().isBlank()) {
            log.warn("GPT chat completions 응답 파싱에 실패했습니다.");
            throw new GptRequestFailException();
        }
    }

    private HttpEntity<GptChatCompletionsRequest> createChatCompletionsRequest(
        GptChatCompletionsRequest bodyEntity) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        return new HttpEntity<>(bodyEntity, headers);
    }
}
