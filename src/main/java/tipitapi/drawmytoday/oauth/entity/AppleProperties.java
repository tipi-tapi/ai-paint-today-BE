package tipitapi.drawmytoday.oauth.entity;

import java.util.Date;
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
    private String iosClientId;
    @Value("${oauth2.apple.ios.client-secret}")
    private String iosClientSecret;
    @Value("${oauth2.apple.ios.token-url}")
    private String tokenUrl;
    @Value("${oauth2.apple.ios.delete-account-url}")
    private String iosDeleteAccountUrl;

    private Date iosClientSecretExpireDate;

}