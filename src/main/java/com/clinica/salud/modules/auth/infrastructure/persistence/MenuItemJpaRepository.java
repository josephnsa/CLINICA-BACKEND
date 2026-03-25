package com.clinica.salud.modules.auth.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface MenuItemJpaRepository extends JpaRepository<MenuItemEntity, UUID> {

    @Query(value = """
            select mi.*
            from menu_items mi
            join role_menu_items rmi on rmi.menu_item_id = mi.id
            join roles r on r.id = rmi.role_id
            where r.code = :roleCode
              and mi.is_active = true
            order by mi.order_index asc
            """, nativeQuery = true)
    List<MenuItemEntity> findActiveMenuByRoleCode(@Param("roleCode") String roleCode);
}

