package se.ifmo.databases.tutor.repositories;

import org.springframework.stereotype.Repository;
import se.ifmo.databases.tutor.models.Role;

@Repository
public interface RoleRepository extends PersistableRepository<Role, Long> {}
