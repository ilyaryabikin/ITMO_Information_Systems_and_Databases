package se.ifmo.databases.tutor.services;

import java.io.InputStream;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.ifmo.databases.tutor.models.Submission;
import se.ifmo.databases.tutor.repositories.SubmissionRepository;
import se.ifmo.databases.tutor.repositories.SubmissionSourceRepository;

@Service
@Transactional
public class SubmissionService extends AbstractService<Submission, Long, SubmissionRepository> {

  private final SubmissionSourceRepository submissionSourceRepository;

  @Autowired
  protected SubmissionService(
      final SubmissionRepository entityRepository,
      final SubmissionSourceRepository submissionSourceRepository) {
    super(entityRepository);
    this.submissionSourceRepository = submissionSourceRepository;
  }

  public Collection<Submission> findAllByLessonId(final Long lessonId) {
    return entityRepository.findAllByLessonId(lessonId);
  }

  public Submission saveEntity(
      final Submission entityToSave, final String mimeType, final InputStream inputStream) {
    final String materialUuid = submissionSourceRepository.save(mimeType, inputStream);
    entityToSave.setUuid(materialUuid);
    return entityRepository.save(entityToSave);
  }

  public String getSource(final Submission submission) {
    return submissionSourceRepository.getPath(submission.getUuid()).toString();
  }

  @Override
  public String getLoggingEntityName() {
    return "Submission";
  }
}
