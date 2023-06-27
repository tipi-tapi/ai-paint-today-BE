package tipitapi.drawmytoday.common.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("!prod")
@Configuration
@ComponentScan(basePackages = {"tipitapi.drawmytoday.dev"})
public class DevConfig {

}
