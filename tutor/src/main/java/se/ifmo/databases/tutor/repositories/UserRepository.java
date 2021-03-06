package se.ifmo.databases.tutor.repositories;

import java.util.Optional;
import org.springframework.stereotype.Repository;
import se.ifmo.databases.tutor.models.User;

@Repository
public interface UserRepository extends PersistableRepository<User, Long> {
  Optional<User> findByEmail(final String email);

  default Optional<User> findByUsername(final String username) {
    return findByEmail(username);
  }
}
