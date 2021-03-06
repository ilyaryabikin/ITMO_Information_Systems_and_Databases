package se.ifmo.databases.tutor.repositories;

import com.vaadin.flow.component.avatar.Avatar;
import org.springframework.stereotype.Repository;

@Repository
public class AvatarSourceRepository extends AbstractFilesystemSourceRepository<Avatar> {

  private static final String AVATARS_SRC = "avatars";

  @Override
  public String getSourceDirectory() {
    return AVATARS_SRC;
  }
}
