package com.clinica.salud.modules.security.infrastructure.web;

import com.clinica.salud.shared.response.ApiResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/security/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    @PersistenceContext
    private final EntityManager entityManager;

    @GetMapping
    @PreAuthorize("hasAuthority('REPORTES_READ')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> list(
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) LocalDateTime from,
            @RequestParam(required = false) LocalDateTime to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        StringBuilder jpql = new StringBuilder("select a from AuditLogEntity a where 1=1 ");

        if (userId != null) {
            jpql.append("and a.userId = :userId ");
        }
        if (action != null && !action.isBlank()) {
            jpql.append("and lower(a.action) like lower(concat('%', :action, '%')) ");
        }
        if (from != null) {
            jpql.append("and a.createdAt >= :from ");
        }
        if (to != null) {
            jpql.append("and a.createdAt <= :to ");
        }

        jpql.append("order by a.createdAt desc");

        TypedQuery<com.clinica.salud.modules.auth.infrastructure.persistence.AuditLogEntity> query =
                entityManager.createQuery(jpql.toString(), com.clinica.salud.modules.auth.infrastructure.persistence.AuditLogEntity.class);

        if (userId != null) {
            query.setParameter("userId", userId);
        }
        if (action != null && !action.isBlank()) {
            query.setParameter("action", action);
        }
        if (from != null) {
            query.setParameter("from", from);
        }
        if (to != null) {
            query.setParameter("to", to);
        }

        query.setFirstResult(page * size);
        query.setMaxResults(size);

        List<com.clinica.salud.modules.auth.infrastructure.persistence.AuditLogEntity> logs = query.getResultList();

        List<Map<String, Object>> items = logs.stream().map(a -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", a.getId());
            m.put("userId", a.getUserId());
            m.put("action", a.getAction());
            m.put("module", a.getModule());
            m.put("entityId", a.getEntityId());
            m.put("details", a.getDetails());
            m.put("ipAddress", a.getIpAddress());
            m.put("createdAt", a.getCreatedAt());
            return m;
        }).toList();

        Map<String, Object> result = new HashMap<>();
        result.put("items", items);
        result.put("total", items.size()); // para MVP

        return ResponseEntity.ok(ApiResponse.ok(result));
    }
}

