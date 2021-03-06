package se.ifmo.databases.tutor.repositories;

import java.io.Serializable;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface PersistableRepository<T extends Persistable<V>, V>
    extends JpaRepository<T, V>, Serializable {}
