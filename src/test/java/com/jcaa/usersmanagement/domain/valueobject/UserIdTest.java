package com.jcaa.usersmanagement.domain.valueobject;

import static org.junit.jupiter.api.Assertions.*;

import com.jcaa.usersmanagement.domain.exception.InvalidUserIdException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("UserId")
class UserIdTest {

  @ParameterizedTest
  @ValueSource(strings = {" user123 ", "  user123  ", "user123\t"})
  @DisplayName("Se crea correctamente eliminando espacios en blanco (trim)")
  void shouldCreateUserIdWithTrimmedValue(String input) {
    // Arrange
    final String expectedUserId = "user123";

    // Act
    final UserId userId = new UserId(input);

    // Assert
    assertEquals(expectedUserId, userId.toString());
  }

  @Test
  @DisplayName("Lanza NullPointerException cuando el ID es null")
  void shouldThrowNullPointerExceptionWhenUserIdIsNull() {
    // Act & Assert
    assertThrows(NullPointerException.class, () -> new UserId(null));
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "   ", "\t", "\n", "\r", "\f", "\b"})
  @DisplayName("Lanza InvalidUserIdException cuando el ID está vacío o en blanco")
  void shouldThrowIllegalArgumentExceptionWhenUserIdIsEmpty(String input) {
    // Act & Assert
    assertThrows(InvalidUserIdException.class, () -> new UserId(input));
  }
}
