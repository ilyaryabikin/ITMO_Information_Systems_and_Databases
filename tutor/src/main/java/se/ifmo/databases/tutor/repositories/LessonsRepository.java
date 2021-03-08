package se.ifmo.databases.tutor.repositories;

import java.time.Instant;
import java.util.Collection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import se.ifmo.databases.tutor.models.Lesson;

@Repository
public interface LessonsRepository extends PersistableRepository<Lesson, Long> {

  @Query("SELECT l FROM lessons l WHERE l.teacher.id = :teacherId")
  Collection<Lesson> findAllByTeacherId(final @Param("teacherId") Long teacherId);

  @Query("SELECT l FROM lessons l WHERE l.teacher.id = :teacherId AND l.endDate > :date")
  Collection<Lesson> findAllByTeacherIdAndEndDateAfterDate(
      final @Param("teacherId") Long teacherId, final @Param("date") Instant now);

  @Query("SELECT l FROM lessons l JOIN l.students s WHERE s.id = :studentId")
  Collection<Lesson> findAllByStudentId(final @Param("studentId") Long studentId);

  @Query("SELECT l FROM lessons l JOIN l.students s WHERE s.id = :studentId AND l.endDate > :date")
  Collection<Lesson> findAllByStudentIdAndEndDateAfterDate(
      final @Param("studentId") Long studentId, final @Param("date") Instant now);
}
