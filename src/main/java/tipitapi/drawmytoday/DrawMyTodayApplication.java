package tipitapi.drawmytoday;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class DrawMyTodayApplication {

    public static void main(String[] args) {
        SpringApplication.run(DrawMyTodayApplication.class, args);
    }

}
