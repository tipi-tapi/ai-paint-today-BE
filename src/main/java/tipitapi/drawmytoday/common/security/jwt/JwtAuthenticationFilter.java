package tipitapi.drawmytoday.common.security.jwt;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.filter.OncePerRequestFilter;
import tipitapi.drawmytoday.common.security.jwt.exception.ExpiredAccessTokenException;
import tipitapi.drawmytoday.common.security.jwt.exception.InvalidTokenException;
import tipitapi.drawmytoday.common.security.jwt.exception.TokenNotFoundException;
import tipitapi.drawmytoday.common.utils.HeaderUtils;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    private final String[] permitAllEndpointList;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        authentication();
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        AntPathMatcher pathMatcher = new AntPathMatcher();

        for (String permitAllEndpoint : permitAllEndpointList) {
            if (pathMatcher.match(permitAllEndpoint, requestURI)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @throws TokenNotFoundException      - 헤더에 토큰이 없는 경우
     * @throws InvalidTokenException       - 헤더에 토큰이 있지만 유효하지 않은 경우
     * @throws ExpiredAccessTokenException - 헤더에 토큰이 있지만 만료된 경우
     */
    private void authentication() {
        String accessToken = HeaderUtils.getJwtToken(getRequest(), JwtType.ACCESS);

        jwtTokenProvider.validAccessToken(accessToken);

        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private HttpServletRequest getRequest() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return servletRequestAttributes.getRequest();
    }
}
