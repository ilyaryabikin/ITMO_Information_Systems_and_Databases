package se.ifmo.databases.tutor.repositories;

import org.springframework.stereotype.Repository;
import se.ifmo.databases.tutor.models.Submission;

@Repository
public class SubmissionSourceRepository extends AbstractFilesystemSourceRepository<Submission> {

  private static final String SUBMISSIONS_SRC = "submissions";

  @Override
  public String getSourceDirectory() {
    return SUBMISSIONS_SRC;
  }
}
