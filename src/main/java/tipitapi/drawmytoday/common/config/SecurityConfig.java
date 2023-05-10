package tipitapi.drawmytoday.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;
import tipitapi.drawmytoday.common.security.jwt.JwtAuthenticationEntryPoint;
import tipitapi.drawmytoday.common.security.jwt.JwtAuthenticationFilter;
import tipitapi.drawmytoday.common.security.jwt.JwtTokenProvider;

@Configuration
@EnableWebSecurity(debug = true)
@AllArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    //    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
            .antMatchers("/swagger-ui/**")
            .antMatchers("/v3/api-docs/**")
            .antMatchers("/oauth2/login")
            .antMatchers("/oauth2/google/login")
            .antMatchers("/oauth2/apple/login")
            .antMatchers("/refresh");
    }

    /**
     * csrf, rememberMe, logout, formLogin, headers 비활성화
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .rememberMe().disable()
            .logout().disable()
            .formLogin().disable()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeHttpRequests(authorize -> authorize
                .antMatchers("/swagger-ui/**").permitAll()
                .antMatchers("/v3/api-docs/**").permitAll()
                .antMatchers("/oauth2/login").permitAll()
                .antMatchers("/oauth2/google/login").permitAll()
                .antMatchers("/oauth2/apple/login").permitAll()
                .antMatchers("/refresh").permitAll())
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(new JwtAuthenticationEntryPoint(objectMapper()),
                JwtAuthenticationFilter.class);

        return http.build();
    }

}
