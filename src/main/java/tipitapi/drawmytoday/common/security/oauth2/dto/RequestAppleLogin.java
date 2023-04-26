package tipitapi.drawmytoday.common.security.oauth2.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RequestAppleLogin {
	private String idToken;
	private String osPlatform;
}
