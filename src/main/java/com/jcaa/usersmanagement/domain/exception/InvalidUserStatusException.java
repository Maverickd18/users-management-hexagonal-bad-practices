package com.jcaa.usersmanagement.domain.exception;

public final class InvalidUserStatusException extends DomainException {


  private static final String ERROR_MESSAGE = "The user status '%s' is not valid.";

  private InvalidUserStatusException(final String message) {
    super(message);
  }

  public static InvalidUserStatusException becauseValueIsInvalid(final String status) {
    return new InvalidUserStatusException(String.format(ERROR_MESSAGE, status));
  }
}
