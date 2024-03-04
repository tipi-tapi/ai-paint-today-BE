package tipitapi.drawmytoday.domain.generator.api.gpt.service;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
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

    public GptService(RestTemplate openaiRestTemplate,
        @Value("${openai.gpt.chat_completions_prompt}") String gptChatCompletionsPrompt) {
        this.openaiRestTemplate = openaiRestTemplate;
        this.chatCompletionsUrl = "https://api.openai.com/v1/chat/completions";
        this.gptChatCompletionsPrompt = gptChatCompletionsPrompt;
    }

    @Override
    @Transactional(noRollbackFor = TextGeneratorException.class)
    public List<Message> generatePrompt(String diaryNote) {
        HttpEntity<GptChatCompletionsRequest> httpEntity = createChatCompletionsRequest(diaryNote);
        ResponseEntity<GptChatCompletionsResponse> responseEntity = null;
        try {
            responseEntity = openaiRestTemplate.postForEntity(
                chatCompletionsUrl, httpEntity, GptChatCompletionsResponse.class);

            validIsSuccessfulRequest(responseEntity);
            List<Message> messages = httpEntity.getBody().getMessages();
            messages.add(responseEntity.getBody().getChoices()[0].getMessage());
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
        String diaryNote) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        GptChatCompletionsRequest bodyEntity = GptChatCompletionsRequest.createFirstMessage(
            gptChatCompletionsPrompt, diaryNote);
        return new HttpEntity<>(bodyEntity, headers);
    }
}
