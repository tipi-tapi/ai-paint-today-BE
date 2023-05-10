package tipitapi.drawmytoday.oauth.properties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@Getter
@NoArgsConstructor
@PropertySource("classpath:application-oauth.yml")
public class GoogleProperties {

    @Value("${oauth2.google.client-id}")
    private String clientId;
    @Value("${oauth2.google.client-secret}")
    private String clientSecret;
    @Value("${oauth2.google.token-url}")
    private String tokenUrl;
    @Value("${oauth2.google.user-info-url}")
    private String userInfoUrl;
    @Value("${oauth2.google.redirect-uri}")
    private String redirectUri;
    @Value("${oauth2.google.delete-account-url}")
    private String deleteAccountUrl;
}