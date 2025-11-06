package com.epam.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class User {
    protected String userId;
    protected String firstName;
    protected String lastName;
    protected String userName;
    protected String password;
    protected boolean isActive;
}
