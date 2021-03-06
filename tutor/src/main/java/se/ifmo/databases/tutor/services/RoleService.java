package se.ifmo.databases.tutor.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.ifmo.databases.tutor.models.Role;
import se.ifmo.databases.tutor.repositories.RoleRepository;

@Service
@Transactional
public class RoleService extends AbstractService<Role, Long, RoleRepository> {

  @Autowired
  protected RoleService(final RoleRepository entityRepository) {
    super(entityRepository);
  }

  @Override
  public String getLoggingEntityName() {
    return "Role";
  }

  @Override
  public Role saveEntity(final Role entityToSave) {
    throw new UnsupportedOperationException("Forbidden to save new roles.");
  }
}
