package se.ifmo.databases.tutor.services;

import java.util.Collection;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.ifmo.databases.tutor.models.Subject;
import se.ifmo.databases.tutor.repositories.SubjectRepository;

@Service
@Transactional
public class SubjectService extends AbstractService<Subject, Long, SubjectRepository> {

  @Autowired
  public SubjectService(final SubjectRepository subjectRepository) {
    super(subjectRepository);
  }

  public Collection<Subject> findAllByTeacherId(final Long teacherId) {
    return entityRepository.findAllByTeacherId(teacherId);
  }

  @Override
  public String getLoggingEntityName() {
    return "Subject Type";
  }
}
