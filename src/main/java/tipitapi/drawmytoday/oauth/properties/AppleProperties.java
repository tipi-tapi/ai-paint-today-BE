package tipitapi.drawmytoday.oauth.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@Getter
@RequiredArgsConstructor
@PropertySource("classpath:application-oauth.yml")
public class AppleProperties {

    private final AppleClientSecret clientSecret;
    private final ApplePrivateKey privateKey;

    @Value("${oauth2.apple.ios.client-id}")
    private String clientId;
    @Value("${oauth2.apple.ios.team-id}")
    private String teamId;
    @Value("${oauth2.apple.ios.key-id}")
    private String keyId;
    @Value("${oauth2.apple.ios.token-url}")
    private String tokenUrl;
    @Value("${oauth2.apple.ios.delete-account-url}")
    private String deleteAccountUrl;

    public String getClientSecret() {
        return clientSecret.getClientSecret(teamId, keyId, privateKey.getPrivateKey(), clientId);
    }
}