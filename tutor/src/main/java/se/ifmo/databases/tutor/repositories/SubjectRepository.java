package se.ifmo.databases.tutor.repositories;

import java.util.Collection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import se.ifmo.databases.tutor.models.Subject;

@Repository
public interface SubjectRepository extends PersistableRepository<Subject, Long> {

  @Query("SELECT s FROM subjects s JOIN s.teachers t WHERE t.id = :teacherId")
  Collection<Subject> findAllByTeacherId(final @Param("teacherId") Long teacherId);
}
