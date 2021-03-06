package se.ifmo.databases.tutor.services;

import static java.lang.String.format;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.ifmo.databases.tutor.repositories.UserRepository;

@Service
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserService implements UserDetailsService {

  private final UserRepository userRepository;

  @Transactional(readOnly = true)
  @Override
  public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
    return userRepository
        .findByEmail(email)
        .orElseThrow(
            () ->
                new UsernameNotFoundException(format("User with email %s was not found.", email)));
  }
}
