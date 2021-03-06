package se.ifmo.databases.tutor.repositories;

import java.util.Collection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import se.ifmo.databases.tutor.models.Submission;

@Repository
public interface SubmissionRepository extends PersistableRepository<Submission, Long> {

  @Query("SELECT s FROM lesson_submissions s WHERE s.lesson.id = :lessonId")
  Collection<Submission> findAllByLessonId(final @Param("lessonId") Long lessonId);
}
