package tipitapi.drawmytoday.domain.oauth.service;

import static tipitapi.drawmytoday.common.exception.ErrorCode.PARSING_ERROR;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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
import tipitapi.drawmytoday.common.utils.HeaderUtils;
import tipitapi.drawmytoday.domain.oauth.dto.OAuthAccessToken;
import tipitapi.drawmytoday.domain.oauth.dto.OAuthUserProfile;
import tipitapi.drawmytoday.domain.oauth.dto.ResponseJwtToken;
import tipitapi.drawmytoday.domain.oauth.properties.GoogleProperties;
import tipitapi.drawmytoday.domain.user.domain.SocialCode;
import tipitapi.drawmytoday.domain.user.domain.User;
import tipitapi.drawmytoday.domain.user.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;


    @Transactional
    public ResponseJwtToken login(HttpServletRequest request) {
        OAuthAccessToken accessToken = getAccessToken(request);

        OAuthUserProfile oAuthUserProfile = getUserProfile(accessToken);

        User user = validateUserService.validateRegisteredUserByEmail(
            oAuthUserProfile.getEmail(), SocialCode.GOOGLE);

        if (user == null) {
            user = userService.registerUser(
                oAuthUserProfile.getEmail(), SocialCode.GOOGLE, accessToken.getRefreshToken(),
                null, oAuthUserProfile.getSub());
        } else {
            boolean dirty = false;
            if (StringUtils.hasText(accessToken.getRefreshToken())) {
                user.setRefreshToken(accessToken.getRefreshToken());
                dirty = true;
            }
            if (StringUtils.hasText(oAuthUserProfile.getSub())) {
                user.setGoogleSub(oAuthUserProfile.getSub());
                dirty = true;
            }
            if (dirty) {
                userRepository.save(user);
            }
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
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("token", user.getRefreshToken());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        String url = properties.getDeleteAccountUrl();
        String response = restTemplate.postForObject(url, request, String.class);

//      구글 탈퇴 실패해도 내부적으로 유저 삭제하도록 정함
//        if (response.contains("error")) {
//            throw new BusinessException(OAUTH_SERVER_FAILED);
//        }

        userService.deleteUser(user);
    }

    private OAuthAccessToken getAccessToken(HttpServletRequest request) {
        String authorizationCode = HeaderUtils.getAuthCode(request);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/x-www-form-urlencoded");
        MultiValueMap<String, String> httpBody = new LinkedMultiValueMap<>();
        httpBody.add("code", authorizationCode);
        httpBody.add("client_id", properties.getClientId());
        httpBody.add("client_secret", properties.getClientSecret());
        httpBody.add("redirect_uri", properties.getRedirectUri());
        httpBody.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> requestToken = new HttpEntity<>(httpBody,
            httpHeaders);

        ResponseEntity<String> response = restTemplate.postForEntity(
            properties.getTokenUrl(), requestToken, String.class);

        try {
            return objectMapper.readValue(response.getBody(), OAuthAccessToken.class);
        } catch (JsonProcessingException e) {
            throw new BusinessException(PARSING_ERROR, e);
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
            throw new BusinessException(PARSING_ERROR, e);
        }
    }

}
