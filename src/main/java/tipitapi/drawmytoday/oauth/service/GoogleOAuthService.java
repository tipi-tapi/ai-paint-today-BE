package tipitapi.drawmytoday.oauth.service;

import static tipitapi.drawmytoday.common.exception.ErrorCode.INTERNAL_SERVER_ERROR;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import tipitapi.drawmytoday.oauth.dto.ResponseAccessToken;
import tipitapi.drawmytoday.oauth.dto.ResponseJwtToken;
import tipitapi.drawmytoday.oauth.dto.UserProfile;
import tipitapi.drawmytoday.oauth.properties.GoogleProperties;
import tipitapi.drawmytoday.oauth.repository.AuthRepository;
import tipitapi.drawmytoday.user.domain.OAuthType;
import tipitapi.drawmytoday.user.domain.User;
import tipitapi.drawmytoday.user.exception.UserNotFoundException;
import tipitapi.drawmytoday.user.repository.UserRepository;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GoogleOAuthService {

    private final GoogleProperties properties;

    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper;

    private final UserRepository userRepository;

    private final AuthRepository authRepository;

    private final JwtTokenProvider jwtTokenProvider;


    @Transactional
    public ResponseJwtToken login(HttpServletRequest request) throws JsonProcessingException {
        // Authorization Code로 Access Token 요청
        ResponseAccessToken accessToken = getAccessToken(request);

        // Access Token으로 User Info 요청
        UserProfile userProfile = getUserProfile(accessToken);

        // save user info to database
        User user = userRepository.findByEmail(userProfile.getEmail())
            .orElseGet(() -> {
                return userRepository.save(User.builder()
                    .email(userProfile.getEmail())
                    .oauthType(OAuthType.GOOGLE)
                    .build());
            });

        // save refresh token to database
        if (StringUtils.hasText(accessToken.getAccessToken())) {
            authRepository.save(new Auth(user, accessToken.getRefreshToken()));
        }

        // // create JWT token
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
        Auth auth = authRepository.findByUser(user).orElseThrow(() -> new UserNotFoundException());
        String refreshToken = auth.getRefreshToken();
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("token", refreshToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        String url = properties.getDeleteAccountUrl();

        String response = restTemplate.postForObject(url, request, String.class);
        if (StringUtils.hasText(response)) {
            throw new BusinessException(INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseAccessToken getAccessToken(HttpServletRequest request)
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

        return objectMapper.readValue(response.getBody(), ResponseAccessToken.class);
    }

    private UserProfile getUserProfile(ResponseAccessToken accessToken)
        throws JsonProcessingException {

        String userInfoUrl = properties.getUserInfoUrl();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken.getAccessToken());

        HttpEntity<?> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<String> userInfoResponse = restTemplate.exchange(userInfoUrl, HttpMethod.GET,
            httpEntity, String.class);

        String userInfo = userInfoResponse.getBody();
        return objectMapper.readValue(userInfo, UserProfile.class);
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