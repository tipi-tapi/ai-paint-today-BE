package tipitapi.drawmytoday.domain.oauth.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApplePrivateKey {

    @Value("${oauth2.apple.ios.private-key}")
    private String privateKey;

    public String getPrivateKey() {
        return "-----BEGIN PRIVATE KEY-----\n"
            + privateKey
            + "\n-----END PRIVATE KEY-----";
    }
}
