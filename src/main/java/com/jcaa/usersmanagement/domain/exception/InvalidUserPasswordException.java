package com.jcaa.usersmanagement.domain.exception;


public final class InvalidUserPasswordException extends DomainException {

  private static final String PASSWORD_EMPTY_MESSAGE = "The user password must not be empty.";
  private static final String PASSWORD_TOO_SHORT_TEMPLATE = "The user password must have at least %d characters.";

  private InvalidUserPasswordException(final String message) {
    super(message);
  }

  public static InvalidUserPasswordException becauseValueIsEmpty() {
    return new InvalidUserPasswordException(PASSWORD_EMPTY_MESSAGE);
  }

  public static InvalidUserPasswordException becauseLengthIsTooShort(final int minimumLength) {
    return new InvalidUserPasswordException(
        String.format(PASSWORD_TOO_SHORT_TEMPLATE, minimumLength));
  }
}