package tipitapi.drawmytoday.oauth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@Schema(description = "AccessToken, RefreshToken Response")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ResponseJwtToken {

    @NotBlank
    @Schema(description = "access token")
    private final String accessToken;

    @NotBlank
    @Schema(description = "refresh token")
    private final String refreshToken;

    public static ResponseJwtToken of(String accessToken, String refreshToken) {
        return new ResponseJwtToken(accessToken, refreshToken);
    }
}
