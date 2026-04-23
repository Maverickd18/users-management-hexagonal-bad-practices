package com.jcaa.usersmanagement.domain.model;

import java.util.Objects;
import lombok.Value;

@Value
public class EmailDestinationModel {

  private static final String ERROR_EMAIL_REQUIRED = "El email del destinatario es requerido.";
  private static final String ERROR_NAME_REQUIRED  = "El nombre del destinatario es requerido.";
  private static final String ERROR_SUBJECT_REQUIRED = "El asunto es requerido.";
  private static final String ERROR_BODY_REQUIRED    = "El cuerpo del mensaje es requerido.";

  String destinationEmail;
  String destinationName;
  String subject;
  String body;

  public EmailDestinationModel(
      final String destinationEmail,
      final String destinationName,
      final String subject,
      final String body) {
    this.destinationEmail = validateNotBlank(destinationEmail, ERROR_EMAIL_REQUIRED);
    this.destinationName  = validateNotBlank(destinationName,  ERROR_NAME_REQUIRED);
    this.subject          = validateNotBlank(subject,          ERROR_SUBJECT_REQUIRED);
    this.body             = validateNotBlank(body,             ERROR_BODY_REQUIRED);
  }

  private static String validateNotBlank(final String value, final String errorMessage) {
    Objects.requireNonNull(value, errorMessage);

    if (value.trim().isEmpty()) {
      throw new IllegalArgumentException(errorMessage);
    }
    return value;
  }
}
