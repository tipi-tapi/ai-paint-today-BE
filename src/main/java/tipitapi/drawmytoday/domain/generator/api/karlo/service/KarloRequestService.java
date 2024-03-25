package tipitapi.drawmytoday.domain.generator.api.karlo.service;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import tipitapi.drawmytoday.domain.diary.dto.CreateTestDiaryRequest.KarloParameter;
import tipitapi.drawmytoday.domain.generator.api.karlo.dto.CreateKarloImageRequest;
import tipitapi.drawmytoday.domain.generator.api.karlo.dto.KarloUrlResponse;
import tipitapi.drawmytoday.domain.generator.api.karlo.exception.KarloRequestFailException;
import tipitapi.drawmytoday.domain.generator.exception.ImageGeneratorException;
import tipitapi.drawmytoday.domain.generator.exception.ImageInputStreamFailException;

@Service
@Transactional(readOnly = true)
class KarloRequestService {

    private final RestTemplate restTemplate;
    private final String karloImageCreateUrl;
    private final String negativePrompt;
    private final HttpHeaders requestHeader;

    public KarloRequestService(RestTemplate karloRestTemplate,
        @Value("${kakao.karlo.image_generate_url}") String karloImageCreateUrl,
        @Value("${kakao.karlo.generate_image.negative_prompt}") String negativePrompt) {
        this.restTemplate = karloRestTemplate;
        this.karloImageCreateUrl = karloImageCreateUrl;
        this.negativePrompt = negativePrompt;
        this.requestHeader = new HttpHeaders() {
            {
                setContentType(MediaType.APPLICATION_JSON);
            }
        };
    }

    byte[] getImageAsUrl(String prompt) throws ImageGeneratorException {
        try {
            HttpEntity<CreateKarloImageRequest> request = getRequest(
                CreateKarloImageRequest.withUrl(prompt, negativePrompt));

            String url = Optional.ofNullable(
                    restTemplate.postForObject(karloImageCreateUrl, request, KarloUrlResponse.class)
                ).orElseThrow(KarloRequestFailException::new)
                .getUrl(0);
            return new URL(url).openStream().readAllBytes();
        } catch (HttpClientErrorException e) {
            throw new KarloRequestFailException(e);
        } catch (IOException e) {
            throw new ImageInputStreamFailException();
        }
    }

    List<byte[]> getTestImageAsUrl(KarloParameter karloParameter) throws ImageGeneratorException {
        try {
            HttpEntity<CreateKarloImageRequest> request = getRequest(
                CreateKarloImageRequest.createTestRequest(karloParameter));

            List<String> imageUrls = Optional.ofNullable(
                    restTemplate.postForObject(karloImageCreateUrl, request, KarloUrlResponse.class)
                ).orElseThrow(KarloRequestFailException::new)
                .getUrls();

            List<byte[]> images = new ArrayList<>();
            for (String url : imageUrls) {
                images.add(new URL(url).openStream().readAllBytes());
            }
            return images;
        } catch (HttpClientErrorException e) {
            throw new KarloRequestFailException(e);
        } catch (IOException e) {
            throw new ImageInputStreamFailException();
        }
    }

    private HttpEntity<CreateKarloImageRequest> getRequest(CreateKarloImageRequest requestDto) {
        return new HttpEntity<>(requestDto, requestHeader);
    }
}
