package tipitapi.drawmytoday.dalle.service;

import java.util.Base64;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import tipitapi.drawmytoday.dalle.dto.CreateImageRequest;
import tipitapi.drawmytoday.dalle.dto.CreateImageResponse;
import tipitapi.drawmytoday.dalle.exception.DallERequestFailException;

@Service
@Transactional(readOnly = true)
public class DallEService {

    private final RestTemplate restTemplate;
    private final String apiUrl;

    public DallEService(@Qualifier("openaiRestTemplate") RestTemplate restTemplate,
        @Value("${openai.dalle.url}") String apiUrl) {
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
    }

    public byte[] getDallEImage(String prompt) {
        CreateImageRequest request = CreateImageRequest.of(prompt);

        CreateImageResponse response = restTemplate
            .postForObject(apiUrl, request, CreateImageResponse.class);
        validateResponse(response);

        return Base64.getDecoder().decode(response.getData().get(0).getB64_json());
    }

    private void validateResponse(CreateImageResponse response) {
        if (response == null || response.getData() == null || response.getData().isEmpty()) {
            throw new DallERequestFailException();
        }
    }
}
