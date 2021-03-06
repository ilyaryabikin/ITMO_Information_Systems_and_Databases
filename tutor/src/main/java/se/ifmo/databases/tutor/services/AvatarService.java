package se.ifmo.databases.tutor.services;

import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.ifmo.databases.tutor.models.Person;
import se.ifmo.databases.tutor.models.User;
import se.ifmo.databases.tutor.repositories.AvatarSourceRepository;

@Service
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class AvatarService {

  private final AvatarSourceRepository avatarSourceRepository;

  public String save(final User user, final String mimeType, final InputStream inputStream) {
    final String avatarUuid = avatarSourceRepository.save(mimeType, inputStream);
    user.setAvatarUuid(avatarUuid);
    return avatarUuid;
  }

  public void save(final Person person, final String mimeType, final InputStream inputStream) {
    save(person.getUser(), mimeType, inputStream);
  }

  public String getSource(final User user) {
    return avatarSourceRepository.getPath(user.getAvatarUuid()).toString();
  }
}
