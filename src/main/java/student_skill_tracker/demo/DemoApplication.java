package student_skill_tracker.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling; // <--- 1. IMPORT THIS
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableScheduling // <--- 2. ADD THIS ANNOTATION
@ComponentScan(basePackages = {"controller", "service", "security", "student_skill_tracker.demo", "scheduler"}) // Added "scheduler" just in case
@EnableJpaRepositories(basePackages = "repository")
@EntityScan(basePackages = "entity")
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}