package se.ifmo.databases.tutor.listeners;

import javax.persistence.PrePersist;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import se.ifmo.databases.tutor.models.User;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserListener {

  private final PasswordEncoder passwordEncoder;

  @PrePersist
  public void encodePassword(final User user) {
    user.setPassword(passwordEncoder.encode(user.getPassword()));
  }
}
