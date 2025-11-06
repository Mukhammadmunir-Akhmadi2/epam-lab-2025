package com.epam.infrastructure.daos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class UserDao {
    protected UUID userId;
    protected String firstName;
    protected String lastName;
    protected String userName;
    protected String password;
    protected boolean isActive;
}
