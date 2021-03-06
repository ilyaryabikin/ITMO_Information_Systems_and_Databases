package se.ifmo.databases.tutor.exceptions;

public class EntityNotFoundException extends Exception {

  public EntityNotFoundException() {
    super();
  }

  public EntityNotFoundException(final String message) {
    super(message);
  }
}
