package io.github.tdees15.gitsync;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "io.github.tdees15.gitsync")
public class GitsyncApplication {

	public static void main(String[] args) {
		SpringApplication.run(GitsyncApplication.class, args);
	}

}
