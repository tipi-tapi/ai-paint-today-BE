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
@EnableWebSecurity
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

    //아.. 이거 deprecate 되니 requestMatchers 써야하는데 왜 안 되는거야
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
            .antMatchers("/")
            .antMatchers("/oauth2/login")
            .antMatchers("/oauth2/google/login")
            .antMatchers("/oauth2/apple/login")
            .antMatchers("/refresh");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors()
            .and()
            .csrf().disable() // h2 console 접속을 위해
            .headers().frameOptions().disable() // h2 console 접속을 위해
            .and()
            .rememberMe().disable()
            .logout().disable()
            .formLogin().disable()
            .headers().disable()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
//
//            .requestMatchers(matchers -> matchers
//                .antMatchers("/")
//                .antMatchers("/oauth2/login")
//                .antMatchers("/oauth2/google/login")
//                .antMatchers("/oauth2/apple/login")
//                .antMatchers("/refresh")
//            )

            .authorizeRequests(
                request -> request.anyRequest().permitAll()
            )

            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(new JwtAuthenticationEntryPoint(objectMapper()),
                JwtAuthenticationFilter.class);

        return http.build();
    }

}
