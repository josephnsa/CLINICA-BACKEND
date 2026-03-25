package com.clinica.salud.modules.auth.domain.port;

import com.clinica.salud.modules.auth.domain.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    Optional<User> findByEmail(String email);

    Optional<User> findById(UUID id);

    User save(User user);

    List<String> findPermissionsByRoleId(UUID roleId);
}
