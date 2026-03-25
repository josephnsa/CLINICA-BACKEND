package com.clinica.salud.modules.auth.infrastructure.persistence;

import com.clinica.salud.modules.auth.domain.model.MenuItem;
import com.clinica.salud.modules.auth.domain.port.MenuRepository;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class MenuRepositoryAdapter implements MenuRepository {

    private final MenuItemJpaRepository menuItemJpaRepository;

    public MenuRepositoryAdapter(MenuItemJpaRepository menuItemJpaRepository) {
        this.menuItemJpaRepository = menuItemJpaRepository;
    }

    @Override
    public List<MenuItem> findMenuByRoleCode(String roleCode) {
        List<MenuItemEntity> entities = menuItemJpaRepository.findActiveMenuByRoleCode(roleCode);

        if (entities.isEmpty()) {
            return List.of();
        }

        // Mapear entidades a modelo de dominio sin hijos todavía
        Map<UUID, MenuItem> mapped = entities.stream()
                .collect(Collectors.toMap(
                        MenuItemEntity::getId,
                        e -> MenuItem.builder()
                                .id(e.getId())
                                .label(e.getLabel())
                                .icon(e.getIcon())
                                .route(e.getRoute())
                                .children(new ArrayList<>())
                                .build()
                ));

        // Construir jerarquía
        List<MenuItem> roots = new ArrayList<>();

        for (MenuItemEntity entity : entities) {
            MenuItem current = mapped.get(entity.getId());
            MenuItemEntity parentEntity = entity.getParent();

            if (parentEntity != null && mapped.containsKey(parentEntity.getId())) {
                mapped.get(parentEntity.getId()).getChildren().add(current);
            } else {
                roots.add(current);
            }
        }

        return roots;
    }
}


