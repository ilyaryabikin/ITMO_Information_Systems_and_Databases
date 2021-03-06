package se.ifmo.databases.tutor.services;

import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.ifmo.databases.tutor.models.Student;
import se.ifmo.databases.tutor.repositories.StudentRepository;

@Service
@Transactional
public class StudentService extends AbstractService<Student, Long, StudentRepository> {

  @Autowired
  public StudentService(final StudentRepository studentRepository) {
    super(studentRepository);
  }

  @Transactional(readOnly = true)
  public Collection<Student> findAllByTeacherId(final Long teacherId) {
    return entityRepository.findAllByTeacherId(teacherId);
  }

  public Collection<Student> findAllByLessonId(final Long lessonId) {
    return entityRepository.findAllByLessonId(lessonId);
  }

  @Override
  public String getLoggingEntityName() {
    return "Student";
  }
}
