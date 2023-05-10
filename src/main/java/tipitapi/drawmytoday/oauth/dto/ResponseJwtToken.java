package tipitapi.drawmytoday.oauth.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@Schema(description = "accessToke, refreshToken Response")
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ResponseJwtToken {

    @Schema(description = "access token")
    private String accessToken;

    @Schema(description = "refresh token(빈 값이 올 수 있다)")
    private String refreshToken;

    public static ResponseJwtToken of(String accessToken, String refreshToken) {
        return new ResponseJwtToken(accessToken, refreshToken);
    }
}
