package tipitapi.drawmytoday.domain.dalle.service;

import java.io.IOException;
import java.net.URL;
import java.util.Base64;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import tipitapi.drawmytoday.domain.dalle.dto.CreateImageRequest;
import tipitapi.drawmytoday.domain.dalle.dto.DallEBase64Response;
import tipitapi.drawmytoday.domain.dalle.dto.DallEUrlResponse;
import tipitapi.drawmytoday.domain.dalle.exception.DallERequestFailException;
import tipitapi.drawmytoday.domain.dalle.exception.ImageInputStreamFailException;

@Service
@Transactional(readOnly = true)
public class DallEService {

    private final RestTemplate restTemplate;
    private final String apiUrl;
    private final HttpHeaders requestHeader;


    public DallEService(@Qualifier("openaiRestTemplate") RestTemplate restTemplate,
        @Value("${openai.dalle.url}") String apiUrl) {
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
        this.requestHeader = new HttpHeaders() {
            {
                setContentType(MediaType.APPLICATION_JSON);
            }
        };
    }

    public byte[] getImageAsBase64(String prompt) throws DallERequestFailException {
        try {
            HttpEntity<CreateImageRequest> request = getRequest(
                CreateImageRequest.withBase64(prompt));

            DallEBase64Response response = Optional.ofNullable(
                restTemplate.postForObject(apiUrl, request, DallEBase64Response.class)
            ).orElseThrow(DallERequestFailException::new);

            return Base64.getDecoder().decode(
                response.getBase64(0)
            );
        } catch (HttpClientErrorException e) {
            throw new DallERequestFailException(e);
        }
    }

    public byte[] getImageAsUrl(String prompt)
        throws DallERequestFailException, ImageInputStreamFailException {
        try {
            HttpEntity<CreateImageRequest> request = getRequest(CreateImageRequest.withUrl(prompt));

            String url = Optional.ofNullable(
                    restTemplate.postForObject(apiUrl, request, DallEUrlResponse.class)
                ).orElseThrow(DallERequestFailException::new)
                .getUrl(0);
            return new URL(url).openStream().readAllBytes();
        } catch (HttpClientErrorException e) {
            throw new DallERequestFailException(e);
        } catch (IOException e) {
            throw new ImageInputStreamFailException();
        }
    }

    private HttpEntity<CreateImageRequest> getRequest(CreateImageRequest requestDto) {
        return new HttpEntity<>(requestDto, requestHeader);
    }
}
