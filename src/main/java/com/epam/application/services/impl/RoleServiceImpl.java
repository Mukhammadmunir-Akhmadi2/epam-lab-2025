package com.epam.application.services.impl;

import com.epam.application.repository.RoleRepository;
import com.epam.application.services.RoleService;
import com.epam.infrastructure.enums.RoleEnum;
import com.epam.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public Role getRole(RoleEnum role) {
        return roleRepository.findByName(role)
                .get();
    }
}