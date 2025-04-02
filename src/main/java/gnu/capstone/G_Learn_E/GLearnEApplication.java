package gnu.capstone.G_Learn_E;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class GLearnEApplication {

	public static void main(String[] args) {
		SpringApplication.run(GLearnEApplication.class, args);
	}

}
