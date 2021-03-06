package se.ifmo.databases.tutor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;

@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class)
public class TutorApplication {

  public static void main(String[] args) {
    SpringApplication.run(TutorApplication.class, args);
  }
}
