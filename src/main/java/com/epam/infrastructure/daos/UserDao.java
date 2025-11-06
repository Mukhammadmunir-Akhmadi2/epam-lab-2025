package com.epam.infrastructure.daos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class UserDao {
    protected String userId;
    protected String firstName;
    protected String lastName;
    protected String userName;
    protected String password;
    protected boolean isActive;
}
