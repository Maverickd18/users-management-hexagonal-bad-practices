package com.jcaa.usersmanagement.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.jcaa.usersmanagement.application.port.out.GetAllUsersPort;
import com.jcaa.usersmanagement.domain.enums.UserRole;
import com.jcaa.usersmanagement.domain.enums.UserStatus;
import com.jcaa.usersmanagement.domain.model.UserModel;
import com.jcaa.usersmanagement.domain.valueobject.UserEmail;
import com.jcaa.usersmanagement.domain.valueobject.UserId;
import com.jcaa.usersmanagement.domain.valueobject.UserName;
import com.jcaa.usersmanagement.domain.valueobject.UserPassword;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Pruebas unitarias para {@link GetAllUsersService}.
 * Cubre los siguientes escenarios:
 * <ul>
 *   <li>Retorno exitoso de la lista de usuarios desde el puerto de salida.</li>
 *   <li>Retorno de lista vacía cuando no existen usuarios registrados.</li>
 * </ul>
 */
@DisplayName("GetAllUsersService Tests")
@ExtendWith(MockitoExtension.class)
class GetAllUsersServiceTest {

  @Mock private GetAllUsersPort getAllUsersPort;

  private GetAllUsersService service;

  @BeforeEach
  void setUp() {
    service = new GetAllUsersService(getAllUsersPort);
  }

  @Test
  @DisplayName("should return list of users from port")
  void shouldReturnUsersFromPort() {
    // Arrange
    final UserModel user =
        new UserModel(
            new UserId("u-001"),
            new UserName("John Arrieta"),
            new UserEmail("john@example.com"),
            UserPassword.fromHash("$2a$12$abcdefghijklmnopqrstuO"),
            UserRole.ADMIN,
            UserStatus.ACTIVE);
    when(getAllUsersPort.getAll()).thenReturn(List.of(user));

    // Act
    final List<UserModel> result = service.execute();

    // Assert
    assertEquals(1, result.size());
    assertSame(user, result.get(0));
  }

  @Test
  @DisplayName("should return empty list when no users exist")
  void shouldReturnEmptyListWhenNoUsers() {
    // Arrange
    when(getAllUsersPort.getAll()).thenReturn(List.of());

    // Act
    final List<UserModel> result = service.execute();

    // Assert
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
}
