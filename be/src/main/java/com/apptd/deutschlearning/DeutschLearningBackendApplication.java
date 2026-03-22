package com.apptd.deutschlearning;

import com.apptd.deutschlearning.config.RenderDatabaseUrlSupport;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
public class DeutschLearningBackendApplication {
  public static void main(String[] args) {
    // Render Docker: đặt spring.datasource.* trước khi Spring/Flyway khởi tạo (DATABASE_URL → JDBC).
    RenderDatabaseUrlSupport.applySystemPropertiesFromOSEnv();
    SpringApplication.run(DeutschLearningBackendApplication.class, args);
  }
}

