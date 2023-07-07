package tipitapi.drawmytoday.common.controller;

import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.DefaultErrorViewResolver;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class ControllerTestConfig {

    @Bean
    public ErrorViewResolver defaultErrorViewResolver(ApplicationContext ac,
        WebProperties webProperties) {
        return new DefaultErrorViewResolver(ac, webProperties.getResources());
    }
}
