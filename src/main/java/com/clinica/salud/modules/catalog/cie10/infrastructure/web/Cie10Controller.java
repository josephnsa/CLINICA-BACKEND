package com.clinica.salud.modules.catalog.cie10.infrastructure.web;

import com.clinica.salud.modules.catalog.cie10.application.dto.Cie10Response;
import com.clinica.salud.modules.catalog.cie10.application.usecase.SearchCie10UseCase;
import com.clinica.salud.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/catalog/cie10")
@RequiredArgsConstructor
public class Cie10Controller {

    private final SearchCie10UseCase searchCie10UseCase;

    @GetMapping
    @PreAuthorize("hasAuthority('CATALOGO_READ')")
    public ResponseEntity<ApiResponse<Page<Cie10Response>>> search(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String q,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<Cie10Response> page = searchCie10UseCase.execute(
                code != null ? code : "",
                q != null ? q : "",
                pageable);
        return ResponseEntity.ok(ApiResponse.ok(page));
    }
}

