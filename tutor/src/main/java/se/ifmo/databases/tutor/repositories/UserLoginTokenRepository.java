package se.ifmo.databases.tutor.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;
import se.ifmo.databases.tutor.models.UserLoginToken;

@Repository
public interface UserLoginTokenRepository extends PersistableRepository<UserLoginToken, Long> {
  @EntityGraph(attributePaths = {"user"})
  Optional<UserLoginToken> findBySeries(final String series);

  void deleteByUserId(final Long userId);
}
