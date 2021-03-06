package se.ifmo.databases.tutor.configs;

import static org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder.BCryptVersion.$2B;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordEncoderConfig {

  @Bean("bcryptPasswordEncoder")
  public PasswordEncoder bcryptPasswordEncoder() {
    return new BCryptPasswordEncoder($2B);
  }
}
