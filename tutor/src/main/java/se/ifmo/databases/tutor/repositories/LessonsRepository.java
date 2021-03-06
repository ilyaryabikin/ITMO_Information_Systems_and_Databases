package se.ifmo.databases.tutor.repositories;

import java.util.Collection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import se.ifmo.databases.tutor.models.Lesson;

@Repository
public interface LessonsRepository extends PersistableRepository<Lesson, Long> {

  @Query("SELECT l FROM lessons l WHERE l.teacher.id = :teacherId")
  Collection<Lesson> findAllByTeacherId(final @Param("teacherId") Long teacherId);

  @Query("SELECT l FROM lessons l JOIN l.students s WHERE s.id = :studentId")
  Collection<Lesson> findAllByStudentId(final @Param("studentId") Long studentId);
}
