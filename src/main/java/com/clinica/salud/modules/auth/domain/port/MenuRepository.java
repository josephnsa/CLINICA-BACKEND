package com.clinica.salud.modules.auth.domain.port;

import com.clinica.salud.modules.auth.domain.model.MenuItem;

import java.util.List;

public interface MenuRepository {

    List<MenuItem> findMenuByRoleCode(String roleCode);
}
