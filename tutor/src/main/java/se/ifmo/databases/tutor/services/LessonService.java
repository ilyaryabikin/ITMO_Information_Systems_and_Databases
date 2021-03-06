package se.ifmo.databases.tutor.services;

import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.ifmo.databases.tutor.models.Lesson;
import se.ifmo.databases.tutor.repositories.LessonsRepository;

@Service
@Transactional
public class LessonService extends AbstractService<Lesson, Long, LessonsRepository> {

  @Autowired
  protected LessonService(final LessonsRepository entityRepository) {
    super(entityRepository);
  }

  public Collection<Lesson> findAllByTeacherId(final Long teacherId) {
    return entityRepository.findAllByTeacherId(teacherId);
  }

  public Collection<Lesson> findAllByStudentId(final Long studentId) {
    return entityRepository.findAllByStudentId(studentId);
  }

  @Override
  public String getLoggingEntityName() {
    return "Lesson";
  }
}
