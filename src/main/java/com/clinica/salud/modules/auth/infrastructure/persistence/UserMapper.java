package com.clinica.salud.modules.auth.infrastructure.persistence;

import com.clinica.salud.modules.auth.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "roleCode", expression = "java(entity.getRole() != null ? entity.getRole().getCode() : null)")
    @Mapping(target = "permissions", expression = "java(com.clinica.salud.modules.auth.infrastructure.persistence.UserMapper.toPermissionCodes(entity))")
    @Mapping(target = "sedeIds", expression = "java(java.util.Collections.emptyList())")
    User toDomain(UserEntity entity);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "lastLogin", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    UserEntity toEntity(User domain);

    static List<String> toPermissionCodes(UserEntity entity) {
        if (entity.getRole() == null || entity.getRole().getPermissions() == null)
            return Collections.emptyList();
        return entity.getRole().getPermissions().stream()
                .map(PermissionEntity::getCode)
                .collect(Collectors.toList());
    }
}
