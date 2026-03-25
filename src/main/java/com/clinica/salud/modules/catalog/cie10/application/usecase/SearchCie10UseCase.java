package com.clinica.salud.modules.catalog.cie10.application.usecase;

import com.clinica.salud.modules.catalog.cie10.application.dto.Cie10Response;
import com.clinica.salud.modules.catalog.cie10.infrastructure.persistence.Cie10CodeEntity;
import com.clinica.salud.modules.catalog.cie10.infrastructure.persistence.Cie10CodeJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchCie10UseCase {

    private final Cie10CodeJpaRepository cie10CodeJpaRepository;

    public Page<Cie10Response> execute(String query, Pageable pageable) {
        String q = query != null ? query.trim() : "";
        Page<Cie10CodeEntity> page = q.isEmpty()
                ? cie10CodeJpaRepository.findAll(pageable)
                : cie10CodeJpaRepository.search(q, pageable);
        return page.map(e -> new Cie10Response(e.getCode(), e.getDescription(), e.getCategory()));
    }
}

