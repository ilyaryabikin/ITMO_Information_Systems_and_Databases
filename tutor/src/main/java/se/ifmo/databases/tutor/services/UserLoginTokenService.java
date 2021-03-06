package se.ifmo.databases.tutor.services;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.util.Date;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.ifmo.databases.tutor.models.User;
import se.ifmo.databases.tutor.models.UserLoginToken;
import se.ifmo.databases.tutor.repositories.UserLoginTokenRepository;
import se.ifmo.databases.tutor.repositories.UserRepository;

@Service
@Transactional
@Slf4j
public class UserLoginTokenService
    extends AbstractService<UserLoginToken, Long, UserLoginTokenRepository>
    implements PersistentTokenRepository {

  private final UserRepository userRepository;

  @Autowired
  public UserLoginTokenService(
      final UserLoginTokenRepository userLoginTokenRepository,
      final UserRepository userRepository) {
    super(userLoginTokenRepository);
    this.userRepository = userRepository;
  }

  @Override
  public void createNewToken(final PersistentRememberMeToken rememberMeToken) {
    final UserLoginToken userLoginToken =
        UserLoginToken.builder()
            .series(rememberMeToken.getSeries())
            .value(rememberMeToken.getTokenValue())
            .lastUsed(rememberMeToken.getDate().toInstant())
            .build();

    final User user =
        userRepository
            .findByEmail(rememberMeToken.getUsername())
            .orElseThrow(
                () ->
                    new EntityNotFoundException(
                        format(
                            "User with email %s was not found during persistence of PersistentRememberMeToken",
                            rememberMeToken.getUsername())));
    userLoginToken.setUser(user);

    entityRepository.save(userLoginToken);
  }

  @Override
  public void updateToken(final String series, final String tokenValue, final Date lastUsed) {
    final UserLoginToken userLoginToken =
        entityRepository
            .findBySeries(series)
            .orElseThrow(
                () ->
                    new EntityNotFoundException(
                        format("Token with series %s was not found", series)));

    userLoginToken.setValue(tokenValue);
    userLoginToken.setLastUsed(lastUsed.toInstant());
  }

  @Transactional(readOnly = true)
  @Nullable
  @Override
  public PersistentRememberMeToken getTokenForSeries(final String seriesId) {
    final Optional<UserLoginToken> optionalToken = entityRepository.findBySeries(seriesId);
    if (optionalToken.isEmpty()) {
      log.error("Token for series {} was not found during token update", seriesId);
      return null;
    }

    final UserLoginToken token = optionalToken.get();
    return new PersistentRememberMeToken(
        token.getUser().getUsername(),
        token.getSeries(),
        token.getValue(),
        Date.from(token.getLastUsed()));
  }

  @Override
  public void removeUserTokens(final String username) {
    final User user =
        userRepository
            .findByUsername(username)
            .orElseThrow(
                () ->
                    new EntityNotFoundException(
                        format("User with username %s was not found", username)));
    entityRepository.deleteByUserId(requireNonNull(user.getId()));
  }

  @Override
  public String getLoggingEntityName() {
    return "UserLoginToken";
  }
}
