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
public class User {

    private UUID id;
    private String email;
    private String password;
    private String fullName;
    private String roleCode;
    private List<String> permissions;
    private List<UUID> sedeIds;
    private boolean isActive;
    private String googleId;
    private String authProvider;
}
