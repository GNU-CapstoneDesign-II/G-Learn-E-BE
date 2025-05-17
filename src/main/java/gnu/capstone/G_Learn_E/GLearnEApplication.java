package gnu.capstone.G_Learn_E;

import gnu.capstone.G_Learn_E.global.fastapi.entity.FastApiProperties;
import gnu.capstone.G_Learn_E.global.security.SecurityPathProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
@EnableJpaAuditing
@EnableConfigurationProperties({FastApiProperties.class, SecurityPathProperties.class})
public class GLearnEApplication {

	public static void main(String[] args) {
		SpringApplication.run(GLearnEApplication.class, args);
	}

}
