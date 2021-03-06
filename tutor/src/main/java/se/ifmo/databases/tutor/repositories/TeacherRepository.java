package se.ifmo.databases.tutor.repositories;

import java.util.Collection;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import se.ifmo.databases.tutor.models.Teacher;

@Repository
public interface TeacherRepository extends PersistablePersonRepository<Teacher> {

  @Query(
      "SELECT t FROM teachers t "
          + "INNER JOIN t.students st "
          + "JOIN FETCH t.subjects "
          + "JOIN FETCH t.user "
          + "WHERE st.id = :studentId")
  Collection<Teacher> findAllByStudentId(final @Param("studentId") Long studentId);

  @EntityGraph(attributePaths = {"user", "user.role", "lessons", "subjects", "students"})
  @Override
  Optional<Teacher> findByUserEmail(final String email);
}
