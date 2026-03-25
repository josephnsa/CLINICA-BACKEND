package com.clinica.salud.modules.auth.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

@Schema(name = "MenuItemDto", description = "Item de menú con hijos opcionales")
public record MenuItemDto(
        @Schema(description = "ID del ítem") UUID id,
        @Schema(description = "Etiqueta visible") String label,
        @Schema(description = "Icono") String icon,
        @Schema(description = "Ruta de navegación") String route,
        @Schema(description = "Subítems del menú", implementation = MenuItemDto.class) List<MenuItemDto> children
) {}
