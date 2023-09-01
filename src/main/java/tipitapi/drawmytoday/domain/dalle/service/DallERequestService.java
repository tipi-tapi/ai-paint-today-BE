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
import tipitapi.drawmytoday.domain.dalle.exception.DallERequestFailException;
import tipitapi.drawmytoday.domain.dalle.exception.ImageInputStreamFailException;

@Service
@Transactional(readOnly = true)
public class DallERequestService {

    private final RestTemplate restTemplate;
    private final String apiUrl;
    private final HttpHeaders requestHeader;

    public DallERequestService(@Qualifier("openaiRestTemplate") RestTemplate restTemplate,
        @Value("${openai.dalle.url}") String apiUrl) {
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
        this.requestHeader = new HttpHeaders() {
            {
                setContentType(MediaType.APPLICATION_JSON);
            }
        };
    }

    byte[] getImageAsUrl(String prompt)
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
