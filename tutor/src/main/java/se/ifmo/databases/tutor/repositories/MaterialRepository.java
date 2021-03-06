package se.ifmo.databases.tutor.repositories;

import java.util.Collection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import se.ifmo.databases.tutor.models.Material;

@Repository
public interface MaterialRepository extends PersistableRepository<Material, Long> {

  @Query("SELECT m FROM materials m WHERE m.lesson.id = :lessonId")
  Collection<Material> findAllByLessonId(final @Param("lessonId") Long lessonId);
}
