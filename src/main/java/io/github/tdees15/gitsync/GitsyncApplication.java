package io.github.tdees15.gitsync;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class GitsyncApplication {

	public static void main(String[] args) {
		SpringApplication.run(GitsyncApplication.class, args);
	}

}
