package tipitapi.drawmytoday.domain.oauth.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@ToString
public class AppleIdToken {

    private String iss;
    private String aud;
    private Long exp;
    private Long iat;
    private String sub;
    private String nonce;
    private String email;
    private Boolean emailVerified;
    private String[] audList;
    private Integer authTime;
    private String nonceSupported;

}
