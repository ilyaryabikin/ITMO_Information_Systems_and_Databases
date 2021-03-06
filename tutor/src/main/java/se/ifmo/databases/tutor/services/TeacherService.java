package se.ifmo.databases.tutor.services;

import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.ifmo.databases.tutor.models.Teacher;
import se.ifmo.databases.tutor.repositories.TeacherRepository;

@Service
@Transactional
public class TeacherService extends AbstractService<Teacher, Long, TeacherRepository> {

  @Autowired
  public TeacherService(final TeacherRepository teacherRepository) {
    super(teacherRepository);
  }

  @Transactional(readOnly = true)
  public Collection<Teacher> findAllByStudentId(final Long studentId) {
    return entityRepository.findAllByStudentId(studentId);
  }

  @Override
  public String getLoggingEntityName() {
    return "Teacher";
  }
}
