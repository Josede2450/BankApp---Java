package bankapp.example.bankapp;

import io.github.cdimascio.dotenv.Dotenv; // Import the dotenv library
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
// This annotation enables component scanning, auto-configuration, and config support
@EnableScheduling
// Enables support for scheduled tasks using @Scheduled
public class BankappApplication {

	public static void main(String[] args) {
		// Load .env file and inject variables into environment before app starts
		Dotenv.configure()
				.ignoreIfMissing() // Will not throw error if .env is not present (safe for prod)
				.load();

		// Start the Spring Boot application
		SpringApplication.run(BankappApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		// Provides a RestTemplate bean for HTTP calls
		return new RestTemplate();
	}
}
