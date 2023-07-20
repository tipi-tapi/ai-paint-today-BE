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
public class AppleProperties {

    @Value("${oauth2.apple.ios.client-id}")
    private String clientId;
    @Value("${oauth2.apple.ios.team-id}")
    private String teamId;
    @Value("${oauth2.apple.ios.key-id}")
    private String keyId;
    @Value("${oauth2.apple.ios.private-key}")
    private String privateKey;
    @Value("${oauth2.apple.ios.token-url}")
    private String tokenUrl;
    @Value("${oauth2.apple.ios.delete-account-url}")
    private String deleteAccountUrl;

    public String getClientSecret() {
        return null;
    }
}