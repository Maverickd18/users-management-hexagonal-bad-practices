package com.jcaa.usersmanagement.application.service.mapper;

import com.jcaa.usersmanagement.application.service.dto.command.CreateUserCommand;
import com.jcaa.usersmanagement.application.service.dto.command.DeleteUserCommand;
import com.jcaa.usersmanagement.application.service.dto.command.UpdateUserCommand;
import com.jcaa.usersmanagement.application.service.dto.query.GetUserByIdQuery;
import com.jcaa.usersmanagement.domain.enums.UserRole;
import com.jcaa.usersmanagement.domain.enums.UserStatus;
import com.jcaa.usersmanagement.domain.model.UserModel;
import com.jcaa.usersmanagement.domain.valueobject.UserEmail;
import com.jcaa.usersmanagement.domain.valueobject.UserId;
import com.jcaa.usersmanagement.domain.valueobject.UserName;
import com.jcaa.usersmanagement.domain.valueobject.UserPassword;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserApplicationMapper {

  public static UserModel fromCreateCommandToModel(final CreateUserCommand command) {
    final String userId   = command.id();
    final String userName = command.name();
    final String email    = command.email();
    final String password = command.password();
    final String role     = command.role();

    return UserModel.create(
        new UserId(userId),
        new UserName(userName),
        new UserEmail(email),
        UserPassword.fromPlainText(password),
        UserRole.fromString(role));
  }

  public static UserModel fromUpdateCommandToModel(
      final UpdateUserCommand command, final UserPassword currentPassword) {

    final UserPassword passwordToUse = (command.password() == null || command.password().isBlank())
        ? currentPassword
        : UserPassword.fromPlainText(command.password());

    final String email = command.email();

    return new UserModel(
        new UserId(command.id()),
        new UserName(command.name()),
        new UserEmail(email),
        passwordToUse,
        UserRole.fromString(command.role()),
        UserStatus.fromString(command.status()));
  }

  public static UserId fromGetUserByIdQueryToUserId(final GetUserByIdQuery query) {
    return new UserId(query.id());
  }

  public static UserId fromDeleteCommandToUserId(final DeleteUserCommand command) {
    return new UserId(command.id());
  }

  public static int roleToCode(final String role) {
    if (role == null || role.isBlank()) {
      throw new IllegalArgumentException("Role must not be null or blank");
    }
    return switch (role.toUpperCase()) {
      case "ADMIN"    -> 1;
      case "MEMBER"   -> 2;
      case "REVIEWER" -> 3;
      default -> throw new IllegalArgumentException("Unknown role: " + role);
    };
  }
}
