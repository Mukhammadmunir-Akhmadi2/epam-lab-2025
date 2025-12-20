package com.epam.application.services;

import com.epam.infrastructure.enums.RoleEnum;
import com.epam.model.Role;

public interface RoleService {
    Role getRole(RoleEnum roleName);
}