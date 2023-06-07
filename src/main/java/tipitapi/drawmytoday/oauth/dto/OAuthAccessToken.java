package tipitapi.drawmytoday.oauth.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * google access token response { access_token: "Sample access token", expires_in: 3599,
 * refresh_token: "Sample refresh token", scope: "https://www.googleapis.com/auth/indexing",
 * token_type: "Bearer" }
 * <p>
 * apple access token response { "access_token": "adg61...67Or9", "token_type": "Bearer",
 * "expires_in": 3600, "refresh_token": "rca7...lABoQ", "id_token": "eyJra...96sZg" }
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OAuthAccessToken {

    private String accessToken;
    private int expiresIn;
    private String refreshToken;
    private String tokenType;

}
