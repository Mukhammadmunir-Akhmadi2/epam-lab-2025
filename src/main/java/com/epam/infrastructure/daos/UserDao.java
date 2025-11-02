package com.epam.infrastructure.daos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class UserDao {
    private String userId;
    private String firstName;
    private String lastName;
    private String userName;
    private String password;
    private boolean isActive;
}
