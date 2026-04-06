package dev.jentic.blog.post07;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot entry point.
 *
 * jentic-spring-boot-starter auto-wires JenticRuntime from application.yml.
 * No explicit runtime setup required — just add the starter and configure.
 *
 * Run with:
 *   mvn spring-boot:run -pl spring-boot
 *
 * Once running, check agent status via Actuator:
 *   curl http://localhost:8080/actuator/health
 */
@SpringBootApplication
public class ConfigurationApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigurationApplication.class, args);
    }
}
