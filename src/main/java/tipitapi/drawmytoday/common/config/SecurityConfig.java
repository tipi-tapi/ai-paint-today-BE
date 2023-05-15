package tipitapi.drawmytoday.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;
import tipitapi.drawmytoday.common.security.jwt.JwtAuthenticationEntryPoint;
import tipitapi.drawmytoday.common.security.jwt.JwtAuthenticationFilter;
import tipitapi.drawmytoday.common.security.jwt.JwtTokenProvider;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    private final String[] permitAllEndpointList = {
        "/swagger-ui/**",
        "/v3/api-docs/**",
        "/oauth2/login",
        "/oauth2/google/login",
        "/oauth2/apple/login",
        "/refresh"
    };

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    /**
     * csrf, rememberMe, logout, formLogin, httpBasic 비활성화
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .rememberMe().disable()
            .logout().disable()
            .formLogin().disable()
            .httpBasic().disable()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, permitAllEndpointList),
                UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(new JwtAuthenticationEntryPoint(objectMapper()),
                JwtAuthenticationFilter.class);

        return http.build();
    }

}
