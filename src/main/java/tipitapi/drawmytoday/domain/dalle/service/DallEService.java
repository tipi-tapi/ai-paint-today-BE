package tipitapi.drawmytoday.domain.dalle.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import tipitapi.drawmytoday.domain.dalle.dto.CreateImageRequest;
import tipitapi.drawmytoday.domain.dalle.dto.DallEUrlResponse;
import tipitapi.drawmytoday.domain.dalle.dto.GeneratedImageAndPrompt;
import tipitapi.drawmytoday.domain.dalle.exception.DallEException;
import tipitapi.drawmytoday.domain.dalle.exception.DallERequestFailException;
import tipitapi.drawmytoday.domain.dalle.exception.ImageInputStreamFailException;
import tipitapi.drawmytoday.domain.diary.service.PromptService;
import tipitapi.drawmytoday.domain.diary.service.PromptTextService;
import tipitapi.drawmytoday.domain.emotion.domain.Emotion;

@Service
@Transactional(readOnly = true)
public class DallEService {

    private final RestTemplate restTemplate;
    private final String apiUrl;
    private final HttpHeaders requestHeader;
    private final PromptTextService promptTextService;
    private final PromptService promptService;


    public DallEService(@Qualifier("openaiRestTemplate") RestTemplate restTemplate,
        @Value("${openai.dalle.url}") String apiUrl, PromptTextService promptTextService,
        PromptService promptService) {
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
        this.requestHeader = new HttpHeaders() {
            {
                setContentType(MediaType.APPLICATION_JSON);
            }
        };
        this.promptTextService = promptTextService;
        this.promptService = promptService;
    }

    @Transactional(noRollbackFor = DallEException.class)
    public GeneratedImageAndPrompt generateImage(Emotion emotion, String keyword)
        throws DallEException {
        String prompt = promptTextService.createPromptText(emotion, keyword);

        try {
            byte[] image = getImageAsUrl(prompt);

            if (image == null) {
                promptService.createPrompt(prompt, false);
                prompt = promptTextService.createPromptText(emotion, null);
                image = getImageAsUrl(prompt);

                if (image == null) { // 재시도하였음에도, 컨텐츠 정책 위반 에러가 발생할경우 이미지 생성 실패 처리
                    throw DallERequestFailException.violatePolicy();
                }
            }

            return new GeneratedImageAndPrompt(prompt, image);
        } catch (DallEException e) {
            promptService.createPrompt(prompt, false);
            throw e;
        }
    }

    private byte[] getImageAsUrl(String prompt)
        throws DallERequestFailException, ImageInputStreamFailException {
        try {
            HttpEntity<CreateImageRequest> request = getRequest(CreateImageRequest.withUrl(prompt));

            String url = Optional.ofNullable(
                    restTemplate.postForObject(apiUrl, request, DallEUrlResponse.class)
                ).orElseThrow(DallERequestFailException::new)
                .getUrl(0);
            return new URL(url).openStream().readAllBytes();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST && isContentPolicyError(e)) {
                return null;
            }
            throw new DallERequestFailException(e);
        } catch (IOException e) {
            throw new ImageInputStreamFailException();
        }
    }

    private boolean isContentPolicyError(HttpClientErrorException e)
        throws DallERequestFailException {
        String responseBody = e.getResponseBodyAsString();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode errorNode = rootNode.path("error");
            if (errorNode.isObject()) {
                String code = errorNode.path("code").asText();
                return code != null && code.equals("content_policy_violation");
            }
        } catch (IOException ioException) {
            throw new DallERequestFailException(ioException);
        }
        return false;
    }

    private HttpEntity<CreateImageRequest> getRequest(CreateImageRequest requestDto) {
        return new HttpEntity<>(requestDto, requestHeader);
    }
}
