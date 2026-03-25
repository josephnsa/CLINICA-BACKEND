package com.clinica.salud.modules.security.infrastructure.web;

import com.clinica.salud.modules.auth.infrastructure.persistence.RoleEntity;
import com.clinica.salud.modules.auth.infrastructure.persistence.RoleJpaRepository;
import com.clinica.salud.modules.auth.infrastructure.persistence.UserEntity;
import com.clinica.salud.modules.auth.infrastructure.persistence.UserJpaRepository;
import com.clinica.salud.shared.response.ApiResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/security/users")
@RequiredArgsConstructor
public class UserController {

    @PersistenceContext
    private final EntityManager entityManager;

    private final RoleJpaRepository roleJpaRepository;
    private final UserJpaRepository userJpaRepository;

    @GetMapping
    @PreAuthorize("hasAuthority('CATALOGO_READ')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> list(
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size
    ) {
        StringBuilder jpql = new StringBuilder("select u from UserEntity u left join fetch u.role r where 1=1 ");

        if (search != null && !search.isBlank()) {
            jpql.append("and (lower(u.email) like lower(concat('%', :search, '%')) ")
                .append("or lower(u.fullName) like lower(concat('%', :search, '%'))) ");
        }
        if (role != null && !role.isBlank()) {
            jpql.append("and r.code = :roleCode ");
        }
        if (active != null) {
            jpql.append("and u.isActive = :active ");
        }

        jpql.append("order by u.fullName asc");

        TypedQuery<UserEntity> query = entityManager.createQuery(jpql.toString(), UserEntity.class);

        if (search != null && !search.isBlank()) {
            query.setParameter("search", search);
        }
        if (role != null && !role.isBlank()) {
            query.setParameter("roleCode", role);
        }
        if (active != null) {
            query.setParameter("active", active);
        }

        query.setFirstResult(page * size);
        query.setMaxResults(size);

        List<UserEntity> users = query.getResultList();

        // Conteo simple (sin filtros de paginación), suficiente para UI básica
        long total = userJpaRepository.count();

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("items", users.stream().map(u -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", u.getId());
            m.put("email", u.getEmail());
            m.put("fullName", u.getFullName());
            RoleEntity roleEntity = u.getRole();
            m.put("roleCode", roleEntity != null ? roleEntity.getCode() : null);
            m.put("roleName", roleEntity != null ? roleEntity.getName() : null);
            m.put("active", u.isActive());
            return m;
        }).toList());

        return ResponseEntity.ok(ApiResponse.ok(result));
    }
}

