package tipitapi.drawmytoday.domain.generator.domain.gpt.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import tipitapi.drawmytoday.domain.generator.domain.gpt.dto.GptChatCompletionsRequest;
import tipitapi.drawmytoday.domain.generator.domain.gpt.dto.GptChatCompletionsResponse;
import tipitapi.drawmytoday.domain.generator.domain.gpt.exception.GptRequestFailException;
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

    @Transactional(noRollbackFor = TextGeneratorException.class)
    public String generateKeyword(String diaryNote) {
        HttpEntity<GptChatCompletionsRequest> httpEntity = createChatCompletionsRequest(diaryNote);
        ResponseEntity<GptChatCompletionsResponse> responseEntity = null;
        try {
            responseEntity = openaiRestTemplate.postForEntity(
                chatCompletionsUrl, httpEntity, GptChatCompletionsResponse.class);

            validIsSuccessfulRequest(responseEntity);
            return responseEntity.getBody().getChoices()[0].getMessage().getContent();
        } catch (RestClientException e) {
            log.warn("GPT chat completions 요청에 실패했습니다. status code: {}, message: {}",
                responseEntity.getStatusCode(), e.getMessage());
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
        headers.add("Content-Type", "application/json");
        GptChatCompletionsRequest bodyEntity = GptChatCompletionsRequest.createFirstMessage(
            gptChatCompletionsPrompt, diaryNote);
        HttpEntity<GptChatCompletionsRequest> httpEntity = new HttpEntity<>(bodyEntity, headers);
        return httpEntity;
    }
}
