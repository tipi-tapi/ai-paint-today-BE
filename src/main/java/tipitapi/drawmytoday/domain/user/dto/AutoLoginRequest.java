package tipitapi.drawmytoday.domain.user.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AutoLoginRequest {

    private final String refreshToken;

}
