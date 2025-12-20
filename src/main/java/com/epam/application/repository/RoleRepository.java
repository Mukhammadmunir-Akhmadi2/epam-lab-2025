package com.epam.application.repository;

import com.epam.infrastructure.enums.RoleEnum;
import com.epam.model.Role;

import java.util.List;
import java.util.Optional;

public interface RoleRepository {
    Optional<Role> findByName(RoleEnum role);
    Role save(Role role);
    List<Role> findAll();
}