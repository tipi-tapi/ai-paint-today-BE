package tipitapi.drawmytoday.oauth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Schema(description = "애플 로그인 request")
public class RequestAppleLogin {

    @NotNull
    @Schema(description = "애플 로그인 시 발급받은 id_token")
    private String idToken;
}
