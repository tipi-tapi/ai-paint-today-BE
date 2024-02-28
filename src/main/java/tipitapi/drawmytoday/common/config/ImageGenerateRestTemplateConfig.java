package tipitapi.drawmytoday.common.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ImageGenerateRestTemplateConfig {

    @Value("${openai.api.key}")
    private String openaiApiKey;

    @Value("${kakao.api.key}")
    private String kakaoApiKey;
    private static final int DEFAULT_CONNECTION_TIMEOUT = 1000 * 30;

    @Bean
    @Qualifier("openaiRestTemplate")
    public RestTemplate openaiRestTemplate() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT);
        RestTemplate restTemplate = new RestTemplate(factory);
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + openaiApiKey);
            return execution.execute(request, body);
        });
        return restTemplate;
    }

    @Bean
    public RestTemplate karloRestTemplate() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT);
        RestTemplate restTemplate = new RestTemplate(factory);
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("Authorization", "KakaoAK " + kakaoApiKey);
            return execution.execute(request, body);
        });
        return restTemplate;
    }
}
