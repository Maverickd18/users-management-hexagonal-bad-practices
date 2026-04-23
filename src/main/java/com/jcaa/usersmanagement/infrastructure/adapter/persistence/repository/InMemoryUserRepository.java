package com.jcaa.usersmanagement.infrastructure.adapter.persistence.repository;

import com.jcaa.usersmanagement.application.port.out.DeleteUserPort;
import com.jcaa.usersmanagement.application.port.out.GetAllUsersPort;
import com.jcaa.usersmanagement.application.port.out.GetUserByEmailPort;
import com.jcaa.usersmanagement.application.port.out.GetUserByIdPort;
import com.jcaa.usersmanagement.application.port.out.SaveUserPort;
import com.jcaa.usersmanagement.application.port.out.UpdateUserPort;
import com.jcaa.usersmanagement.domain.model.UserModel;
import com.jcaa.usersmanagement.domain.valueobject.UserEmail;
import com.jcaa.usersmanagement.domain.valueobject.UserId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory implementation of user repository ports for development and testing.
 */
public final class InMemoryUserRepository
    implements SaveUserPort,
        UpdateUserPort,
        GetUserByIdPort,
        GetUserByEmailPort,
        GetAllUsersPort,
        DeleteUserPort {

  private final Map<String, UserModel> usersById = new HashMap<>();
  private final Map<String, String> idByEmail = new HashMap<>();

  @Override
  public UserModel save(final UserModel user) {
    usersById.put(user.idValue(), user);
    idByEmail.put(user.emailValue(), user.idValue());
    return user;
  }

  @Override
  public UserModel update(final UserModel user) {
    // In-memory update is just putting it again
    return save(user);
  }

  @Override
  public Optional<UserModel> getById(final UserId userId) {
    return Optional.ofNullable(usersById.get(userId.value()));
  }

  @Override
  public Optional<UserModel> getByEmail(final UserEmail email) {
    final String id = idByEmail.get(email.value());
    return id == null ? Optional.empty() : getById(new UserId(id));
  }

  @Override
  public List<UserModel> getAll() {
    return new ArrayList<>(usersById.values());
  }

  @Override
  public void delete(final UserId userId) {
    final UserModel user = usersById.remove(userId.value());
    if (user != null) {
      idByEmail.remove(user.emailValue());
    }
  }
}
