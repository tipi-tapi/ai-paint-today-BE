package tipitapi.drawmytoday.domain.generator.api.google.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import tipitapi.drawmytoday.domain.generator.service.TranslateTextService;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleTranslatorService implements TranslateTextService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;
    private static final String GOOGLE_TRANSLATE_URL = "https://translate.googleapis.com/translate_a/single";

    @Override
    public String translateAutoToEnglish(String text) {
        String translatedText = null;
        try {
            String jsonResult = restTemplate.getForObject(
                GOOGLE_TRANSLATE_URL + "?client=gtx&sl=auto&tl=en&dt=t&dt=bd&dj=1&source=icon&q="
                    + text, String.class);
            JsonNode rootNode = objectMapper.readTree(jsonResult);
            if (rootNode.get("sentences") != null) {
                translatedText = rootNode.get("sentences").get(0).get("trans").asText();
            } else {
                translatedText = rootNode.get("dict").get(0).get("terms").get(0).asText();
            }
        } catch (RestClientException e) {
            log.error("구글 번역 API 요청 실패: {}", text, e);
            throw e;
        } catch (JsonProcessingException e) {
            log.error("구글 번역 API 응답 파싱 실패: {}", text, e);
            throw new RuntimeException("구글 번역 API 응답 파싱 실패: " + text, e);
        }
        return translatedText;
    }
}
