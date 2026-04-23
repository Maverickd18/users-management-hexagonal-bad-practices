package com.jcaa.usersmanagement.infrastructure.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

public final class AppProperties {

  private static final String PROPERTIES_FILE = "application.properties";

  private final Properties properties;

  public AppProperties() {
    this(AppProperties.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE));
  }

  // Package-private — test entry point
  AppProperties(final InputStream stream) {
    this.properties = doLoad(stream);
  }

  private static Properties doLoad(final InputStream stream) {
    Objects.requireNonNull(stream, "File not found in classpath: " + PROPERTIES_FILE);

    final Properties loadedProperties = new Properties();
    try (stream) {
      loadedProperties.load(stream);
    } catch (final IOException exception) {
      throw ConfigurationException.becauseLoadFailed(exception);
    }
    return loadedProperties;
  }

  public String get(final String key) {
    final String value = properties.getProperty(key);
    return Objects.requireNonNull(value, "Property not found in " + PROPERTIES_FILE + ": " + key);
  }

  public int getInt(final String key) {
    final String value = get(key);
    try {
      return Integer.parseInt(value);
    } catch (final NumberFormatException exception) {
      throw ConfigurationException.becauseValueIsInvalid(key, value, exception);
    }
  }
}
