package tipitapi.drawmytoday.oauth.service;

import static tipitapi.drawmytoday.common.exception.ErrorCode.OAUTH_SERVER_FAILED;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.http.HttpServletRequest;
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
import tipitapi.drawmytoday.common.exception.ErrorCode;
import tipitapi.drawmytoday.common.security.jwt.JwtTokenProvider;
import tipitapi.drawmytoday.common.security.jwt.exception.InvalidTokenException;
import tipitapi.drawmytoday.common.security.jwt.exception.TokenNotFoundException;
import tipitapi.drawmytoday.oauth.domain.Auth;
import tipitapi.drawmytoday.oauth.dto.OAuthAccessToken;
import tipitapi.drawmytoday.oauth.dto.OAuthUserProfile;
import tipitapi.drawmytoday.oauth.dto.ResponseJwtToken;
import tipitapi.drawmytoday.oauth.exception.OAuthNotFoundException;
import tipitapi.drawmytoday.oauth.properties.GoogleProperties;
import tipitapi.drawmytoday.oauth.repository.AuthRepository;
import tipitapi.drawmytoday.user.domain.SocialCode;
import tipitapi.drawmytoday.user.domain.User;
import tipitapi.drawmytoday.user.service.UserService;
import tipitapi.drawmytoday.user.service.ValidateUserService;

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
    public ResponseJwtToken login(HttpServletRequest request) throws JsonProcessingException {
        OAuthAccessToken accessToken = getAccessToken(request);
        OAuthUserProfile oAuthUserProfile = getUserProfile(accessToken);

        User user = validateUserService.validateRegisteredUserByEmail(
            oAuthUserProfile.getEmail(), SocialCode.GOOGLE);

        if (user == null) {
            user = registerUser(oAuthUserProfile, accessToken);
        }

        // create JWT token
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
        String refreshToken = auth.getRefreshToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("token", refreshToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        String url = properties.getDeleteAccountUrl();

        String response = restTemplate.postForObject(url, request, String.class);
        if (response.contains("error")) {
            throw new BusinessException(OAUTH_SERVER_FAILED);
        }

        user.deleteUser();
    }


    private User registerUser(OAuthUserProfile oAuthUserProfile, OAuthAccessToken accessToken) {
        User user = userService.registerUser(oAuthUserProfile.getEmail(), SocialCode.GOOGLE);
        authRepository.save(new Auth(user, accessToken.getRefreshToken()));
        return user;
    }

    private OAuthAccessToken getAccessToken(HttpServletRequest request)
        throws JsonProcessingException {
        String authorizationCode = getAuthorizationCode(request);

        String tokenUri = properties.getTokenUrl();

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

        ResponseEntity<String> response = restTemplate.postForEntity(tokenUri, requestToken,
            String.class);

        return objectMapper.readValue(response.getBody(), OAuthAccessToken.class);
    }

    private OAuthUserProfile getUserProfile(OAuthAccessToken accessToken)
        throws JsonProcessingException {

        String userInfoUrl = properties.getUserInfoUrl();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken.getAccessToken());

        HttpEntity<?> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<String> userInfoResponse = restTemplate.exchange(userInfoUrl, HttpMethod.GET,
            httpEntity, String.class);

        String userInfo = userInfoResponse.getBody();
        return objectMapper.readValue(userInfo, OAuthUserProfile.class);
    }


    private String getAuthorizationCode(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (!StringUtils.hasText(authorization)) {
            throw new TokenNotFoundException(ErrorCode.AUTH_CODE_NOT_FOUND);
        }

        String[] tokens = StringUtils.delimitedListToStringArray(authorization, " ");
        if (tokens.length != 2 || !"Bearer".equals(tokens[0])) {
            throw new InvalidTokenException();
        }
        return tokens[1];
    }
}