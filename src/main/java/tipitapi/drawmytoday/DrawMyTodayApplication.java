package tipitapi.drawmytoday;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
@ComponentScan(
    basePackages = {"tipitapi.drawmytoday"},
    excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "tipitapi.drawmytoday.dev.*")
)
public class DrawMyTodayApplication {

    public static void main(String[] args) {
        SpringApplication.run(DrawMyTodayApplication.class, args);
    }

}
