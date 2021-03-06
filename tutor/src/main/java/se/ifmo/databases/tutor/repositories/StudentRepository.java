package se.ifmo.databases.tutor.repositories;

import java.util.Collection;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import se.ifmo.databases.tutor.models.Student;

@Repository
public interface StudentRepository extends PersistablePersonRepository<Student> {

  @Query(
      "SELECT s FROM students s "
          + "INNER JOIN s.teachers st "
          + "JOIN FETCH s.user "
          + "WHERE st.id = :teacherId")
  Collection<Student> findAllByTeacherId(final @Param("teacherId") Long teacherId);

  @Query("SELECT s FROM students s JOIN s.lessons l WHERE l.id = :lessonId")
  Collection<Student> findAllByLessonId(final @Param("lessonId") Long lessonId);

  @EntityGraph(attributePaths = {"user", "user.role", "lessons", "teachers"})
  @Override
  Optional<Student> findByUserEmail(final String username);
}
