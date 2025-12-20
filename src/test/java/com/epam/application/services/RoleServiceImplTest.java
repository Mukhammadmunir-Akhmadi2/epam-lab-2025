package com.epam.application.services;

import com.epam.application.repository.RoleRepository;
import com.epam.application.services.impl.RoleServiceImpl;
import com.epam.infrastructure.enums.RoleEnum;
import com.epam.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    private Role existingRole;

    @BeforeEach
    void setUp() {
        existingRole = new Role();
        existingRole.setRole(RoleEnum.TRAINEE);
    }

    @Test
    void getRole_shouldReturnExistingRole() {
        when(roleRepository.findByName(RoleEnum.TRAINEE))
                .thenReturn(Optional.of(existingRole));

        Role result = roleService.getRole(RoleEnum.TRAINEE);

        assertNotNull(result);
        assertEquals(RoleEnum.TRAINEE, result.getRole());

        verify(roleRepository, times(1)).findByName(RoleEnum.TRAINEE);
    }

    @Test
    void getRole_shouldThrowExceptionWhenRoleNotFound() {
        when(roleRepository.findByName(RoleEnum.TRAINEE))
                .thenReturn(Optional.empty());

        assertThrows(Exception.class, () ->
                roleService.getRole(RoleEnum.TRAINEE)
        );

        verify(roleRepository, times(1)).findByName(RoleEnum.TRAINEE);
    }
}