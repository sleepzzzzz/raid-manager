package web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
@PropertySource(value = {"config.properties"})
public class RaidManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(RaidManagerApplication.class, args);
	}

}