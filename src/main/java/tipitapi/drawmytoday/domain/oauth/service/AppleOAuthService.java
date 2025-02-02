package tipitapi.drawmytoday.domain.oauth.service;

import static tipitapi.drawmytoday.common.exception.ErrorCode.APPLE_EMAIL_NOT_FOUND;
import static tipitapi.drawmytoday.common.exception.ErrorCode.OAUTH_SERVER_FAILED;
import static tipitapi.drawmytoday.common.exception.ErrorCode.PARSING_ERROR;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Base64;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import tipitapi.drawmytoday.common.exception.BusinessException;
import tipitapi.drawmytoday.common.exception.ErrorCode;
import tipitapi.drawmytoday.common.security.jwt.JwtTokenProvider;
import tipitapi.drawmytoday.common.utils.HeaderUtils;
import tipitapi.drawmytoday.domain.oauth.domain.Auth;
import tipitapi.drawmytoday.domain.oauth.dto.AppleIdToken;
import tipitapi.drawmytoday.domain.oauth.dto.OAuthAccessToken;
import tipitapi.drawmytoday.domain.oauth.dto.RequestAppleLogin;
import tipitapi.drawmytoday.domain.oauth.dto.ResponseJwtToken;
import tipitapi.drawmytoday.domain.oauth.exception.OAuthNotFoundException;
import tipitapi.drawmytoday.domain.oauth.properties.AppleProperties;
import tipitapi.drawmytoday.domain.oauth.repository.AuthRepository;
import tipitapi.drawmytoday.domain.user.domain.SocialCode;
import tipitapi.drawmytoday.domain.user.domain.User;
import tipitapi.drawmytoday.domain.user.service.UserService;
import tipitapi.drawmytoday.domain.user.service.ValidateUserService;

@Slf4j
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
    public ResponseJwtToken login(HttpServletRequest request, RequestAppleLogin requestAppleLogin) {
        OAuthAccessToken oAuthAccessToken = getAccessToken(request);
        AppleIdToken appleIdToken = getAppleIdToken(requestAppleLogin.getIdToken());

        if (appleIdToken.getEmail() == null) {
            throw new BusinessException(APPLE_EMAIL_NOT_FOUND);
        }

        User user = validateUserService.validateRegisteredUserByEmail(
            appleIdToken.getEmail(), SocialCode.APPLE);

        if (user == null) {
            user = userService.registerUser(
                appleIdToken.getEmail(), SocialCode.APPLE, oAuthAccessToken.getRefreshToken());
        } else {
            if (StringUtils.hasText(oAuthAccessToken.getRefreshToken())) {
                Auth auth = authRepository.findByUser(user)
                    .orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR));
                auth.setRefreshToken(oAuthAccessToken.getRefreshToken());
            }
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

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", properties.getClientId());
        body.add("client_secret", properties.getClientSecret());
        body.add("token", auth.getRefreshToken());
        body.add("token_type_hint", "refresh_token");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        String url = properties.getDeleteAccountUrl();
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new BusinessException(OAUTH_SERVER_FAILED, new Throwable(response.getBody()));
        }

        user.deleteUser();
    }

    private OAuthAccessToken getAccessToken(HttpServletRequest request) {
        String authorizationCode = HeaderUtils.getAuthCode(request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("code", authorizationCode);
        body.add("client_id", properties.getClientId());
        body.add("client_secret", properties.getClientSecret());
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
            properties.getTokenUrl(), entity, String.class);

        try {
            return objectMapper.readValue(response.getBody(), OAuthAccessToken.class);
        } catch (JsonProcessingException e) {
            throw new BusinessException(PARSING_ERROR, e);
        }
    }

    private AppleIdToken getAppleIdToken(String idToken) {
        String[] jwtParts = idToken.split("\\.");
        byte[] bytes = Base64.getDecoder().decode(jwtParts[1].getBytes());
        try {
            return objectMapper.readValue(bytes, AppleIdToken.class);
        } catch (IOException e) {
            throw new BusinessException(PARSING_ERROR, e);
        }
    }

}
