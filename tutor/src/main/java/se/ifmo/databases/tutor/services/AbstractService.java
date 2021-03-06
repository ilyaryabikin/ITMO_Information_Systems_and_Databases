package se.ifmo.databases.tutor.services;

import static java.lang.String.format;
import static lombok.AccessLevel.PROTECTED;

import java.io.Serializable;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Persistable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.ifmo.databases.tutor.exceptions.EntityNotFoundException;
import se.ifmo.databases.tutor.repositories.PersistableRepository;

@Service
@RequiredArgsConstructor(access = PROTECTED)
public abstract class AbstractService<
        T extends Persistable<V>, V, R extends PersistableRepository<T, V>>
    implements Serializable {

  protected final R entityRepository;

  @Transactional(readOnly = true)
  public T findEntityById(final V id) throws EntityNotFoundException {
    return entityRepository
        .findById(id)
        .orElseThrow(
            () ->
                new EntityNotFoundException(
                    format("Entity %s with id %s was not found.", getLoggingEntityName(), id)));
  }

  @Transactional(readOnly = true)
  public List<T> findAllEntities() {
    return entityRepository.findAll();
  }

  @Transactional
  public T saveEntity(final T entityToSave) {
    return entityRepository.save(entityToSave);
  }

  @Transactional
  public void deleteEntity(final T entityToDelete) {
    entityRepository.delete(entityToDelete);
  }

  public abstract String getLoggingEntityName();
}
