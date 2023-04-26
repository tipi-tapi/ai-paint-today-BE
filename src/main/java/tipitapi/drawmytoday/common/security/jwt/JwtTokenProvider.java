package tipitapi.drawmytoday.common.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import tipitapi.drawmytoday.common.exception.BusinessException;
import tipitapi.drawmytoday.common.exception.ErrorCode;
import tipitapi.drawmytoday.common.security.jwt.exception.ExpiredAccessTokenException;
import tipitapi.drawmytoday.common.security.jwt.exception.ExpiredRefreshTokenException;
import tipitapi.drawmytoday.common.security.jwt.exception.InvalidTokenException;
import tipitapi.drawmytoday.user.domain.UserRole;

@Slf4j
@Component
@PropertySource("classpath:application-oauth.yml")
public class JwtTokenProvider {

    private final String CLAIM_USER_ID = JwtProperties.USER_ID;
    private final String CLAIM_USER_ROLE = JwtProperties.ROLE;

    private final long ACCESS_TOKEN_EXPIRE_TIME;
    private final long REFRESH_TOKEN_EXPIRE_TIME;

    private final Key key;

    public JwtTokenProvider(@Value("${jwt.access-token-expire-time}") long accessTime,
        @Value("${jwt.refresh-token-expire-time}") long refreshTime,
        @Value("${jwt.secret}") String secretKey) {
        this.ACCESS_TOKEN_EXPIRE_TIME = accessTime;
        this.REFRESH_TOKEN_EXPIRE_TIME = refreshTime;
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    protected String createToken(Long userId, UserRole userRole, long tokenValid) {

        Map<String, Object> header = new HashMap<>();
        header.put("typ", "JWT");

        Claims claims = Jwts.claims();

        claims.put(CLAIM_USER_ID, userId.toString());
        claims.put(CLAIM_USER_ROLE, userRole);

        Date date = new Date();

        return Jwts.builder()
            .setHeader(header)
            .setClaims(claims) // 토큰 발행 유저 정보
            .setIssuedAt(date) // 토큰 발행 시간
            .setExpiration(new Date(date.getTime() + tokenValid)) // 토큰 만료 시간
            .signWith(SignatureAlgorithm.HS512, key)
            .compact();// 알고리즘과 키 설정
    }

    public String createAccessToken(Long userId, UserRole userRole) {
        return createToken(userId, userRole, ACCESS_TOKEN_EXPIRE_TIME);
    }

    public String createRefreshToken(Long userId, UserRole userRole) {
        return createToken(userId, userRole, REFRESH_TOKEN_EXPIRE_TIME);
    }

    public String createNewAccessTokenFromRefreshToken(String refreshToken) {
        Claims claims = parseClaims(refreshToken);

        Long userId = Long.parseLong((String) claims.get(CLAIM_USER_ID));
        UserRole role = UserRole.valueOf((String) claims.get(CLAIM_USER_ROLE));
        return createAccessToken(userId, role);
    }

    /**
     * 쿠키 maxAge는 초단위 설정이라 1000으로 나눈값으로 설정
     */
    public long getExpireTime() {
        return REFRESH_TOKEN_EXPIRE_TIME / 1000;
    }

    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        if (claims.get(CLAIM_USER_ROLE) == null || !StringUtils.hasText(
            claims.get(CLAIM_USER_ROLE).toString())) {
            throw new BusinessException(ErrorCode.AUTHORITY_NOT_FOUND); //유저권한없음
        }

        log.debug("access claims : username={}, authority={}", claims.getSubject(),
            claims.get(CLAIM_USER_ROLE));

        Collection<? extends GrantedAuthority> authorities =
            Arrays.stream(claims.get(CLAIM_USER_ROLE).toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new JwtAuthenticationToken(claims, "", authorities);
    }

    public void validAccessToken(String token) {
        try {
            parseClaims(token);
        } catch (ExpiredJwtException e) {
            throw new ExpiredAccessTokenException();
        } catch (Exception e) {
            throw new InvalidTokenException(e);
        }
    }

    public void validRefreshToken(String token) {
        try {
            parseClaims(token);
        } catch (ExpiredJwtException e) {
            throw new ExpiredRefreshTokenException();
        } catch (Exception e) {
            throw new InvalidTokenException(e);
        }
    }

    public Claims parseClaims(String accessToken) {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(accessToken)
            .getBody();
        // return Jwts.parser()
        // 	.setSigningKey(key)
        // 	.parseClaimsJws(accessToken)
        // 	.getBody();
    }

}
