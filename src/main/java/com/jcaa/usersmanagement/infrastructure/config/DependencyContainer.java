package com.jcaa.usersmanagement.infrastructure.config;

import com.jcaa.usersmanagement.application.port.in.CreateUserUseCase;
import com.jcaa.usersmanagement.application.port.in.DeleteUserUseCase;
import com.jcaa.usersmanagement.application.port.in.GetAllUsersUseCase;
import com.jcaa.usersmanagement.application.port.in.GetUserByIdUseCase;
import com.jcaa.usersmanagement.application.port.in.LoginUseCase;
import com.jcaa.usersmanagement.application.port.in.UpdateUserUseCase;
import com.jcaa.usersmanagement.application.port.out.DeleteUserPort;
import com.jcaa.usersmanagement.application.port.out.GetAllUsersPort;
import com.jcaa.usersmanagement.application.port.out.GetUserByEmailPort;
import com.jcaa.usersmanagement.application.port.out.GetUserByIdPort;
import com.jcaa.usersmanagement.application.port.out.SaveUserPort;
import com.jcaa.usersmanagement.application.port.out.UpdateUserPort;
import com.jcaa.usersmanagement.application.service.CreateUserService;
import com.jcaa.usersmanagement.application.service.DeleteUserService;
import com.jcaa.usersmanagement.application.service.EmailNotificationService;
import com.jcaa.usersmanagement.application.service.GetAllUsersService;
import com.jcaa.usersmanagement.application.service.GetUserByIdService;
import com.jcaa.usersmanagement.application.service.LoginService;
import com.jcaa.usersmanagement.application.service.UpdateUserService;
import com.jcaa.usersmanagement.infrastructure.adapter.email.JavaMailEmailSenderAdapter;
import com.jcaa.usersmanagement.infrastructure.adapter.email.SmtpConfig;
import com.jcaa.usersmanagement.infrastructure.adapter.persistence.config.DatabaseConfig;
import com.jcaa.usersmanagement.infrastructure.adapter.persistence.config.DatabaseConnectionFactory;
import com.jcaa.usersmanagement.infrastructure.adapter.persistence.exception.PersistenceException;
import com.jcaa.usersmanagement.infrastructure.adapter.persistence.repository.InMemoryUserRepository;
import com.jcaa.usersmanagement.infrastructure.adapter.persistence.repository.UserRepositoryMySQL;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.controller.UserController;
import lombok.extern.java.Log;

import java.sql.Connection;
import jakarta.validation.Validator;

@Log
public final class DependencyContainer {

  private static final String DB_HOST = "db.host";
  private static final String DB_PORT = "db.port";
  private static final String DB_NAME = "db.name";
  private static final String DB_USER = "db.username";
  private static final String DB_PASSWORD = "db.password";

  private static final String SMTP_HOST = "smtp.host";
  private static final String SMTP_PORT = "smtp.port";
  private static final String SMTP_USER = "smtp.username";
  private static final String SMTP_PASSWORD = "smtp.password";
  private static final String SMTP_FROM = "smtp.from.address";
  private static final String SMTP_FROM_NAME = "smtp.from.name";
  private static final String MAILTRAP_SANDBOX = "mailtrap.sandbox";
  private static final String MAILTRAP_INBOX_ID = "mailtrap.inbox.id";

  private final UserController userController;

  public DependencyContainer() {
    final AppProperties properties = new AppProperties();

    final PersistenceContext context = buildPersistenceContext(properties);
    final var userRepository = context.repository();

    final JavaMailEmailSenderAdapter emailSender =
        new JavaMailEmailSenderAdapter(buildSmtpConfig(properties));
    final EmailNotificationService emailNotification = new EmailNotificationService(emailSender);

    // Construir Validator para las validaciones en la capa de aplicación
    final Validator validator = ValidatorProvider.buildValidator();

    final CreateUserUseCase createUserUseCase =
        new CreateUserService(
            (SaveUserPort) userRepository,
            (GetUserByEmailPort) userRepository,
            emailNotification,
            validator);

    final UpdateUserUseCase updateUserUseCase =
        new UpdateUserService(
            (UpdateUserPort) userRepository,
            (GetUserByIdPort) userRepository,
            (GetUserByEmailPort) userRepository,
            emailNotification,
            validator);

    final DeleteUserUseCase deleteUserUseCase =
        new DeleteUserService(
            (DeleteUserPort) userRepository,
            (GetUserByIdPort) userRepository,
            validator);

    final GetUserByIdUseCase getUserByIdUseCase =
        new GetUserByIdService((GetUserByIdPort) userRepository, validator);

    final GetAllUsersUseCase getAllUsersUseCase =
        new GetAllUsersService((GetAllUsersPort) userRepository);

    final LoginUseCase loginUseCase =
        new LoginService((GetUserByEmailPort) userRepository, validator);

    this.userController =
        new UserController(
            createUserUseCase,
            updateUserUseCase,
            deleteUserUseCase,
            getUserByIdUseCase,
            getAllUsersUseCase,
            loginUseCase);
  }

  private PersistenceContext buildPersistenceContext(final AppProperties properties) {
    try {
      final Connection connection = buildDatabaseConnection(properties);
      log.info("Database connection established successfully.");
      return new PersistenceContext(new UserRepositoryMySQL(connection));
    } catch (final PersistenceException exception) {
      log.warning("Could not connect to database: " + exception.getMessage());
      log.warning("Falling back to InMemoryUserRepository for this session.");
      return new PersistenceContext(new InMemoryUserRepository());
    }
  }

  private record PersistenceContext(Object repository) {}

  public UserController userController() {
    return userController;
  }

  private static Connection buildDatabaseConnection(final AppProperties properties) {
    final DatabaseConfig config =
        new DatabaseConfig(
            properties.get(DB_HOST),
            properties.getInt(DB_PORT),
            properties.get(DB_NAME),
            properties.get(DB_USER),
            properties.get(DB_PASSWORD));
    return DatabaseConnectionFactory.createConnection(config);
  }

  private static SmtpConfig buildSmtpConfig(final AppProperties properties) {
    return new SmtpConfig(
        properties.get(SMTP_HOST),
        properties.getInt(SMTP_PORT),
        properties.get(SMTP_USER),
        properties.get(SMTP_PASSWORD),
        properties.get(SMTP_FROM),
        properties.get(SMTP_FROM_NAME),
        Boolean.parseBoolean(properties.getOrDefault(MAILTRAP_SANDBOX, "false")),
        parseOptionalLong(properties.getOrDefault(MAILTRAP_INBOX_ID, null)));
  }

  private static Long parseOptionalLong(final String value) {
    if (value == null || value.isBlank() || value.contains("placeholder")) {
      return null;
    }
    try {
      return Long.parseLong(value);
    } catch (NumberFormatException e) {
      return null;
    }
  }
}
