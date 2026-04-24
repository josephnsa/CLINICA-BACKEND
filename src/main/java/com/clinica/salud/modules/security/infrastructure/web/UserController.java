package com.clinica.salud.modules.security.infrastructure.web;

import com.clinica.salud.modules.auth.infrastructure.persistence.RoleEntity;
import com.clinica.salud.modules.auth.infrastructure.persistence.RoleJpaRepository;
import com.clinica.salud.modules.auth.infrastructure.persistence.UserEntity;
import com.clinica.salud.modules.auth.infrastructure.persistence.UserJpaRepository;
import com.clinica.salud.modules.security.application.dto.UserCreateRequest;
import com.clinica.salud.modules.security.application.dto.UserUpdateRequest;
import com.clinica.salud.shared.exception.DomainException;
import com.clinica.salud.shared.response.ApiResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/security/users")
@RequiredArgsConstructor
public class UserController {

    @PersistenceContext
    private final EntityManager entityManager;

    private final RoleJpaRepository roleJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    @PreAuthorize("hasAuthority('USUARIOS_READ')")
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
        result.put("items", users.stream().map(this::toSummaryMap).toList());

        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('USUARIOS_WRITE')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> create(@Valid @RequestBody UserCreateRequest body) {
        String email = body.email().trim().toLowerCase(Locale.ROOT);
        if (userJpaRepository.findByEmail(email).isPresent()) {
            throw new DomainException("Ya existe un usuario con ese email");
        }
        RoleEntity role = roleJpaRepository.findById(body.roleId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Rol no encontrado"));

        boolean active = body.active() == null || body.active();
        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(body.password()));
        user.setFullName(body.fullName().trim());
        user.setRole(role);
        user.setActive(active);
        UserEntity saved = userJpaRepository.save(user);

        return ResponseEntity.ok(ApiResponse.ok(toSummaryMap(saved)));
    }

    @RequestMapping(value = "/{id}", method = {RequestMethod.PUT, RequestMethod.PATCH})
    @PreAuthorize("hasAuthority('USUARIOS_WRITE')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UserUpdateRequest body
    ) {
        UserEntity user = userJpaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Usuario no encontrado"));
        RoleEntity role = roleJpaRepository.findById(body.roleId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Rol no encontrado"));

        user.setFullName(body.fullName().trim());
        user.setRole(role);
        user.setActive(body.active());
        UserEntity saved = userJpaRepository.save(user);

        return ResponseEntity.ok(ApiResponse.ok(toSummaryMap(saved)));
    }

    private Map<String, Object> toSummaryMap(UserEntity u) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", u.getId());
        m.put("email", u.getEmail());
        m.put("fullName", u.getFullName());
        RoleEntity roleEntity = u.getRole();
        m.put("roleCode", roleEntity != null ? roleEntity.getCode() : null);
        m.put("roleName", roleEntity != null ? roleEntity.getName() : null);
        m.put("active", u.isActive());
        return m;
    }
}

