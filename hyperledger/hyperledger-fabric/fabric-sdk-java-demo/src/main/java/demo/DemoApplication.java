package demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        System.setProperty("spring.profiles.active", "mariadb");
        SpringApplication.run(DemoApplication.class, args);
    }
}

