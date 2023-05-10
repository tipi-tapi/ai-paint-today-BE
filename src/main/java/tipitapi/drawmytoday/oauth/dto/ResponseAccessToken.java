package tipitapi.drawmytoday.oauth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@Schema(description = "JWT AccessToke Response")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ResponseAccessToken {

    @Schema(description = "access token")
    private String accessToken;

    public static ResponseAccessToken of(String accessToken) {
        return new ResponseAccessToken(accessToken);
    }
}
