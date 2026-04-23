package com.jcaa.usersmanagement.infrastructure.config;

public final class ConfigurationException extends RuntimeException {

  private static final String ERROR_MESSAGE = "Failed to load the application configuration.";

  private ConfigurationException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public static ConfigurationException becauseLoadFailed(final Throwable cause) {
    return new ConfigurationException(ERROR_MESSAGE, cause);
  }

  public static ConfigurationException becauseValueIsInvalid(final String key, final String value, final Throwable cause) {
    return new ConfigurationException(
        String.format("La propiedad '%s' tiene un valor inválido: '%s'", key, value), cause);
  }
}
