package tipitapi.drawmytoday.domain.oauth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@Schema(description = "JWT AccessToke Response")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ResponseAccessToken {

    @NotBlank
    @Schema(description = "access token")
    private final String accessToken;

    public static ResponseAccessToken of(String accessToken) {
        return new ResponseAccessToken(accessToken);
    }
}
