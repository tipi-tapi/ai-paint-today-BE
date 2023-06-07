package tipitapi.drawmytoday.common.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;
import tipitapi.drawmytoday.common.exception.ErrorCode;
import tipitapi.drawmytoday.common.response.ErrorResponse;
import tipitapi.drawmytoday.common.security.jwt.exception.TokenException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (TokenException e) {
            log.warn("exception info={}", e.getErrorCode(), e);
            ErrorCode errorCode = e.getErrorCode();
            response.setStatus(e.getErrorCode().getStatus());
            ErrorResponse errorResponse = new ErrorResponse(errorCode.getCode(),
                errorCode.getMessage(), null);
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        }
    }

}
