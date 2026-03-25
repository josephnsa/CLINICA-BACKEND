package com.clinica.salud.modules.auth.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuItem {

    private UUID id;
    private String label;
    private String icon;
    private String route;
    private List<MenuItem> children;
}
