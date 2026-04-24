package com.clinica.salud.modules.security.infrastructure.web;

import com.clinica.salud.modules.auth.infrastructure.persistence.RoleEntity;
import com.clinica.salud.modules.auth.infrastructure.persistence.RoleJpaRepository;
import com.clinica.salud.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/security/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleJpaRepository roleJpaRepository;

    @GetMapping
    @PreAuthorize("hasAuthority('USUARIOS_READ')")
    public ResponseEntity<ApiResponse<List<RoleEntity>>> list() {
        List<RoleEntity> roles = roleJpaRepository.findAll();
        return ResponseEntity.ok(ApiResponse.ok(roles));
    }
}

