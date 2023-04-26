package tipitapi.drawmytoday.common.security.jwt;

import java.io.IOException;
import java.util.Objects;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.filter.OncePerRequestFilter;
import tipitapi.drawmytoday.common.exception.ErrorCode;
import tipitapi.drawmytoday.common.security.jwt.exception.ExpiredAccessTokenException;
import tipitapi.drawmytoday.common.security.jwt.exception.InvalidTokenException;
import tipitapi.drawmytoday.common.security.jwt.exception.JwtTokenNotFoundException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    private static HttpServletRequest getRequest() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return servletRequestAttributes.getRequest();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        authentication();
        filterChain.doFilter(request, response);
    }

    /**
     * @throws JwtTokenNotFoundException   - 헤더에 토큰이 없는 경우
     * @throws InvalidTokenException       - 헤더에 토큰이 있지만 유효하지 않은 경우
     * @throws ExpiredAccessTokenException - 헤더에 토큰이 있지만 만료된 경우
     */
    private void authentication() {
        String accessToken = getAccessToken();

        jwtTokenProvider.validAccessToken(accessToken);

        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String getAccessToken() {
        String authorization = getRequest().getHeader(JwtProperties.ACCESS_TOKEN_HEADER);

        if (Objects.isNull(authorization)) {
            throw new JwtTokenNotFoundException(ErrorCode.JWT_ACCESS_TOKEN_NOT_FOUND);
        }

        String[] tokens = StringUtils.delimitedListToStringArray(authorization, " ");

        if (tokens.length != 2 || !"Bearer".equals(tokens[0])) {
            throw new InvalidTokenException();
        }

        return tokens[1];
    }
}
