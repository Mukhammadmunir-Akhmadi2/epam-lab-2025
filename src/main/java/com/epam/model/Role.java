package com.epam.model;

import com.epam.infrastructure.enums.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Role {
    private String roleId;
    private RoleEnum role;
}
