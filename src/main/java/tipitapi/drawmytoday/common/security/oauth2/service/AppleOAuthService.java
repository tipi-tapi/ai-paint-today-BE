package tipitapi.drawmytoday.common.security.oauth2.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Base64;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import tipitapi.drawmytoday.common.security.jwt.JwtTokenProvider;
import tipitapi.drawmytoday.common.security.oauth2.dto.AppleIdToken;
import tipitapi.drawmytoday.common.security.oauth2.dto.RequestAppleLogin;
import tipitapi.drawmytoday.common.security.oauth2.dto.ResponseAccessToken;
import tipitapi.drawmytoday.common.security.oauth2.dto.ResponseJwtToken;
import tipitapi.drawmytoday.common.security.oauth2.entity.AppleProperties;
import tipitapi.drawmytoday.user.domain.Auth;
import tipitapi.drawmytoday.user.domain.OAuthType;
import tipitapi.drawmytoday.user.domain.User;
import tipitapi.drawmytoday.user.repository.AuthRepository;
import tipitapi.drawmytoday.user.repository.UserRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AppleOAuthService {

    private final AppleProperties properties;

    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper;

    private final UserRepository userRepository;

    private final AuthRepository authRepository;

    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public ResponseJwtToken login(HttpServletRequest request, RequestAppleLogin requestAppleLogin)
        throws IOException {
        // authorization code 가져오기
        String authorizationCode = getAuthorizationCode(request);

        // authorization code로 refresh token 가져오기
        ResponseAccessToken responseAccessToken = getRefreshToken(authorizationCode,
            requestAppleLogin.getOsPlatform());

        // appleIdToken 파싱
        AppleIdToken appleIdToken = getAppleIdToken(requestAppleLogin.getIdToken());

        // save user info to database
        User user = userRepository.findByEmail(appleIdToken.getEmail())
            .orElseGet(() -> {
                return userRepository.save(User.builder()
                    .email(appleIdToken.getEmail())
                    .oauthType(OAuthType.APPLE)
                    .build());
            });

        // save refresh token to database
        authRepository.save(new Auth(user, responseAccessToken.getRefreshToken()));

        // // create JWT token
        String jwtAccessToken = jwtTokenProvider.createAccessToken(user.getUserId(),
            user.getUserRole());
        String jwtRefreshToken = jwtTokenProvider.createRefreshToken(user.getUserId(),
            user.getUserRole());

        return ResponseJwtToken.of(jwtAccessToken, jwtRefreshToken);
    }

    @Transactional
    public void deleteAccount(User user) {
        Auth auth = authRepository.findByUser(user)
            .orElseThrow(() -> new RuntimeException("User refresh token not found"));
        String refreshToken = auth.getRefreshToken();

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", properties.getIosClientId());
        body.add("client_secret", properties.getIosClientSecret());
        body.add("token", refreshToken);
        body.add("token_type_hint", "refresh_token");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        String url = "https://appleid.apple.com/auth/revoke";

        String response = restTemplate.postForObject(url, request, String.class);
        if (!StringUtils.hasText(response)) {
            throw new RuntimeException("Failed to delete account");
        }
    }

    private ResponseAccessToken getRefreshToken(String authorizationCode, String osPlatform)
        throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

//        나중에 android도 애플로그인 지원하면 추가
//        String clientId =
//            osPlatform.equals("ios") ? properties.getIosClientId() : properties.getWebClientId();
//        String clientSecret = osPlatform.equals("ios") ? properties.getIosClientSecret()
//            : properties.getWebClientSecret();

        String clientId = properties.getIosClientId();
        String clientSecret = properties.getIosClientSecret();
        String appleTokenUrl = "https://appleid.apple.com/auth/token";

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("code", authorizationCode);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(appleTokenUrl, entity,
            String.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Failed to get access token from Google");
        }

        String tokenResponse = response.getBody();
        return objectMapper.readValue(tokenResponse, ResponseAccessToken.class);
    }

    private AppleIdToken getAppleIdToken(String idToken) throws IOException {
        String[] jwtParts = idToken.split("\\.");
        byte[] bytes = Base64.getDecoder().decode(jwtParts[1].getBytes());
        ObjectMapper mapper = new ObjectMapper();
        AppleIdToken appleIDToken = mapper.readValue(bytes, AppleIdToken.class);
        return appleIDToken;
    }

    private String getAuthorizationCode(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        Assert.hasText(authorization, "Authorization header must not be empty");

        String[] tokens = StringUtils.delimitedListToStringArray(authorization, " ");
        Assert.isTrue(tokens.length == 2, "Authorization header must be two tokens");
        Assert.isTrue("Bearer".equals(tokens[0]), "Authorization header must start with Bearer");
        return tokens[1];
    }
}
