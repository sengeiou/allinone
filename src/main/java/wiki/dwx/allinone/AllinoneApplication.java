package wiki.dwx.allinone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class AllinoneApplication {

	public static void main(String[] args) {
		SpringApplication.run(AllinoneApplication.class, args);
	}

}
