package tipitapi.drawmytoday.oauth.service;

import static tipitapi.drawmytoday.common.exception.ErrorCode.AUTH_CODE_NOT_FOUND;
import static tipitapi.drawmytoday.common.exception.ErrorCode.INTERNAL_SERVER_ERROR;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Base64;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import tipitapi.drawmytoday.common.exception.BusinessException;
import tipitapi.drawmytoday.common.security.jwt.JwtTokenProvider;
import tipitapi.drawmytoday.common.security.jwt.exception.InvalidTokenException;
import tipitapi.drawmytoday.common.security.jwt.exception.TokenNotFoundException;
import tipitapi.drawmytoday.oauth.domain.Auth;
import tipitapi.drawmytoday.oauth.dto.AppleIdToken;
import tipitapi.drawmytoday.oauth.dto.OAuthAccessToken;
import tipitapi.drawmytoday.oauth.dto.RequestAppleLogin;
import tipitapi.drawmytoday.oauth.dto.ResponseJwtToken;
import tipitapi.drawmytoday.oauth.exception.OAuthNotFoundException;
import tipitapi.drawmytoday.oauth.properties.AppleProperties;
import tipitapi.drawmytoday.oauth.repository.AuthRepository;
import tipitapi.drawmytoday.user.domain.SocialCode;
import tipitapi.drawmytoday.user.domain.User;
import tipitapi.drawmytoday.user.service.UserService;
import tipitapi.drawmytoday.user.service.ValidateUserService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AppleOAuthService {

    private final AppleProperties properties;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final ValidateUserService validateUserService;
    private final UserService userService;
    private final AuthRepository authRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public ResponseJwtToken login(HttpServletRequest request, RequestAppleLogin requestAppleLogin)
        throws IOException {
        String authorizationCode = getAuthorizationCode(request);

        OAuthAccessToken oAuthAccessToken = getRefreshToken(authorizationCode);

        AppleIdToken appleIdToken = getAppleIdToken(requestAppleLogin.getIdToken());

        User user = validateUserService.validateRegisteredUserByEmail(
            appleIdToken.getEmail(), SocialCode.APPLE);
        if (user != null) {
            Auth auth = authRepository.findByUser(user).orElseThrow(OAuthNotFoundException::new);
            auth.setRefreshToken(oAuthAccessToken.getRefreshToken());
        } else {
            user = userService.registerUser(appleIdToken.getEmail(), SocialCode.APPLE);
            authRepository.save(new Auth(user, oAuthAccessToken.getRefreshToken()));
        }

        String jwtAccessToken = jwtTokenProvider.createAccessToken(user.getUserId(),
            user.getUserRole());
        String jwtRefreshToken = jwtTokenProvider.createRefreshToken(user.getUserId(),
            user.getUserRole());

        return ResponseJwtToken.of(jwtAccessToken, jwtRefreshToken);
    }

    @Transactional
    public void deleteAccount(User user) {
        Auth auth = authRepository.findByUser(user).orElseThrow(OAuthNotFoundException::new);
        String refreshToken = auth.getRefreshToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", properties.getIosClientId());
        body.add("client_secret", properties.getIosClientSecret());
        body.add("token", refreshToken);
        body.add("token_type_hint", "refresh_token");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        String url = properties.getIosDeleteAccountUrl();

        String response = restTemplate.postForObject(url, request, String.class);
        if (response != null) {
            throw new BusinessException(INTERNAL_SERVER_ERROR);
        }

        user.deleteUser();
    }

    private OAuthAccessToken getRefreshToken(String authorizationCode)
        throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String clientId = properties.getIosClientId();
        String clientSecret = properties.getIosClientSecret();
        String appleTokenUrl = properties.getTokenUrl();

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("code", authorizationCode);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(appleTokenUrl, entity,
            String.class);

        String tokenResponse = response.getBody();
        return objectMapper.readValue(tokenResponse, OAuthAccessToken.class);
    }

    private AppleIdToken getAppleIdToken(String idToken) throws IOException {
        String[] jwtParts = idToken.split("\\.");
        byte[] bytes = Base64.getDecoder().decode(jwtParts[1].getBytes());
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(bytes, AppleIdToken.class);
    }

    private String getAuthorizationCode(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (!StringUtils.hasText(authorization)) {
            throw new TokenNotFoundException(AUTH_CODE_NOT_FOUND);
        }

        String[] tokens = StringUtils.delimitedListToStringArray(authorization, " ");
        if (tokens.length != 2 || !"Bearer".equals(tokens[0])) {
            throw new InvalidTokenException();
        }
        return tokens[1];
    }
}
