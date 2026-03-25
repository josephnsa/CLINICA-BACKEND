package com.clinica.salud.modules.auth.infrastructure.persistence;

import com.clinica.salud.modules.auth.domain.model.User;
import com.clinica.salud.modules.auth.domain.port.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository userJpaRepository;
    private final RoleJpaRepository roleJpaRepository;
    private final UserMapper userMapper;

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email).map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return userJpaRepository.findById(id).map(userMapper::toDomain);
    }

    @Override
    public User save(User user) {
        UserEntity entity = userMapper.toEntity(user);
        if (user.getRoleCode() != null && !user.getRoleCode().isBlank()) {
            roleJpaRepository.findAll().stream()
                    .filter(r -> user.getRoleCode().equals(r.getCode()))
                    .findFirst()
                    .ifPresent(entity::setRole);
        }
        entity = userJpaRepository.save(entity);
        return userMapper.toDomain(entity);
    }

    @Override
    public List<String> findPermissionsByRoleId(UUID roleId) {
        return roleJpaRepository.findById(roleId)
                .map(r -> {
                    if (r.getPermissions() == null) return Collections.<String>emptyList();
                    return r.getPermissions().stream().map(PermissionEntity::getCode).toList();
                })
                .orElse(Collections.emptyList());
    }
}
