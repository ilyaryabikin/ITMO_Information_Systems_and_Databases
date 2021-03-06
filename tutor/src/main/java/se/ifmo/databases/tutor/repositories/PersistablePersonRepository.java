package se.ifmo.databases.tutor.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.NoRepositoryBean;
import se.ifmo.databases.tutor.models.Person;

@NoRepositoryBean
public interface PersistablePersonRepository<T extends Person>
    extends PersistableRepository<T, Long> {
  Optional<T> findByUserEmail(final String email);

  @EntityGraph(attributePaths = {"user", "user.role"})
  default Optional<T> findByUserUsername(final String username) {
    return findByUserEmail(username);
  }
}
