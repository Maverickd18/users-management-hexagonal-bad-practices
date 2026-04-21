package com.jcaa.usersmanagement.domain.enums;

import com.jcaa.usersmanagement.domain.exception.InvalidUserStatusException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserStatus {
  ACTIVE("Activo"),
  INACTIVE("Inactivo"),
  PENDING("Pendiente de activacion"),
  BLOCKED("Bloqueado"),
  DELETED("Eliminado");

  private final String displayLabel;

  public static UserStatus fromString(final String value) {
    for (final UserStatus status : values()) {
      if (status.name().equalsIgnoreCase(value)) {
        return status;
      }
    }
    throw InvalidUserStatusException.becauseValueIsInvalid(value);
  }
}
