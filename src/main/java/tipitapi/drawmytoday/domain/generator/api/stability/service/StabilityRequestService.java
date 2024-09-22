package tipitapi.drawmytoday.domain.generator.api.stability.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import tipitapi.drawmytoday.domain.generator.api.stability.dto.CreateStabilityImageRequest;
import tipitapi.drawmytoday.domain.generator.api.stability.exception.StabilityRequestFailException;
import tipitapi.drawmytoday.domain.generator.exception.ImageGeneratorException;

@Service
public class StabilityRequestService {

    private final RestTemplate restTemplate;
    private final String stableImageCoreUrl;
    private final String negativePrompt;

    public StabilityRequestService(RestTemplate stabilityRestTemplate,
        @Value("${stability.stable_image_core.url}") String stableImageCoreUrl,
        @Value("${stability.stable_image_core.negative_prompt}") String negativePrompt) {
        this.restTemplate = stabilityRestTemplate;
        this.stableImageCoreUrl = stableImageCoreUrl;
        this.negativePrompt = negativePrompt;

    }

    byte[] generateImage(String prompt) throws ImageGeneratorException {
        try {
            var requestEntity = CreateStabilityImageRequest.getRequest(prompt, negativePrompt);

            ResponseEntity<byte[]> response = restTemplate.exchange(stableImageCoreUrl,
                HttpMethod.POST, requestEntity, byte[].class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new StabilityRequestFailException();
            }

            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new StabilityRequestFailException(e);
        }
    }
}
