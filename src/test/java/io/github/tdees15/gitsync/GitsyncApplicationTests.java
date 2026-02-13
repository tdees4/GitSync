package io.github.tdees15.gitsync;

import io.github.tdees15.gitsync.config.TestJdaConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestJdaConfig.class)
class GitsyncApplicationTests {

	@Test
	void contextLoads() {
		System.out.println("Testing context");
	}

}
