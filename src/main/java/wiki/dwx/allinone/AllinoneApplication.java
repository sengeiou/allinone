package wiki.dwx.allinone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
//@ComponentScan(basePackages = {"wiki.dwx.allinone.schedule"})
public class AllinoneApplication {

	public static void main(String[] args) {
		SpringApplication.run(AllinoneApplication.class, args);
	}

}
