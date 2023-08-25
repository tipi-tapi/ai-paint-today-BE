package tipitapi.drawmytoday.domain.oauth.service;

import static tipitapi.drawmytoday.common.exception.ErrorCode.OAUTH_SERVER_FAILED;
import static tipitapi.drawmytoday.common.exception.ErrorCode.OBJECT_MAPPING_ERROR;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import tipitapi.drawmytoday.common.exception.BusinessException;
import tipitapi.drawmytoday.common.security.jwt.JwtTokenProvider;
import tipitapi.drawmytoday.common.utils.HeaderUtils;
import tipitapi.drawmytoday.domain.oauth.domain.Auth;
import tipitapi.drawmytoday.domain.oauth.dto.OAuthAccessToken;
import tipitapi.drawmytoday.domain.oauth.dto.OAuthUserProfile;
import tipitapi.drawmytoday.domain.oauth.dto.ResponseJwtToken;
import tipitapi.drawmytoday.domain.oauth.exception.OAuthNotFoundException;
import tipitapi.drawmytoday.domain.oauth.properties.GoogleProperties;
import tipitapi.drawmytoday.domain.oauth.repository.AuthRepository;
import tipitapi.drawmytoday.domain.user.domain.SocialCode;
import tipitapi.drawmytoday.domain.user.domain.User;
import tipitapi.drawmytoday.domain.user.service.UserService;
import tipitapi.drawmytoday.domain.user.service.ValidateUserService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GoogleOAuthService {

    private final GoogleProperties properties;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final UserService userService;
    private final ValidateUserService validateUserService;
    private final AuthRepository authRepository;
    private final JwtTokenProvider jwtTokenProvider;


    @Transactional
    public ResponseJwtToken login(HttpServletRequest request) {
        OAuthAccessToken accessToken = getAccessToken(request);
        OAuthUserProfile oAuthUserProfile = getUserProfile(accessToken);

        User user = validateUserService.validateRegisteredUserByEmail(
            oAuthUserProfile.getEmail(), SocialCode.GOOGLE);

        if (user == null) {
            user = userService.registerUser(
                oAuthUserProfile.getEmail(), SocialCode.GOOGLE, accessToken.getRefreshToken());
        }

        String jwtAccessToken = jwtTokenProvider.createAccessToken(user.getUserId(),
            user.getUserRole());
        String jwtRefreshToken = jwtTokenProvider.createRefreshToken(user.getUserId(),
            user.getUserRole());

        return ResponseJwtToken.of(jwtAccessToken, jwtRefreshToken);
    }

    /**
     * delete account success: response = "" delete account fail: response =
     * {"error":"invalid_token","error_description":"Invalid Value"}
     *
     * @param user
     */
    @Transactional
    public void deleteAccount(User user) {
        Auth auth = authRepository.findByUser(user).orElseThrow(OAuthNotFoundException::new);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        Map<String, String> body = new LinkedHashMap<>();
        body.put("token", auth.getRefreshToken());

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        String url = properties.getDeleteAccountUrl();
        String response = restTemplate.postForObject(url, request, String.class);

        if (response != null && response.contains("error")) {
            throw new BusinessException(OAUTH_SERVER_FAILED);
        }

        user.deleteUser();
    }

    private OAuthAccessToken getAccessToken(HttpServletRequest request) {
        String authorizationCode = HeaderUtils.getAuthorizationHeader(request);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/x-www-form-urlencoded");
        Map<String, String> httpBody = new LinkedHashMap<>();
        httpBody.put("code", authorizationCode);
        httpBody.put("client_id", properties.getClientId());
        httpBody.put("client_secret", properties.getClientSecret());
        httpBody.put("redirect_uri", properties.getRedirectUri());
        httpBody.put("grant_type", "authorization_code");

        HttpEntity<Map<String, String>> requestToken = new HttpEntity<>(httpBody, httpHeaders);

        ResponseEntity<String> response = restTemplate.postForEntity(
            properties.getTokenUrl(), requestToken, String.class);

        try {
            return objectMapper.readValue(response.getBody(), OAuthAccessToken.class);
        } catch (JsonProcessingException e) {
            throw new BusinessException(OBJECT_MAPPING_ERROR, e);
        }
    }

    private OAuthUserProfile getUserProfile(OAuthAccessToken accessToken) {

        String userInfoUrl = properties.getUserInfoUrl();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken.getAccessToken());

        HttpEntity<?> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<String> userInfoResponse = restTemplate.exchange(userInfoUrl, HttpMethod.GET,
            httpEntity, String.class);

        try {
            return objectMapper.readValue(userInfoResponse.getBody(), OAuthUserProfile.class);
        } catch (JsonProcessingException e) {
            throw new BusinessException(OBJECT_MAPPING_ERROR, e);
        }
    }

}