package bankapp.example.bankapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test") // <- this tells Spring to load application-test.properties
class BankappApplicationTests {

	@Test
	void contextLoads() {
		assert true;
	}
}
