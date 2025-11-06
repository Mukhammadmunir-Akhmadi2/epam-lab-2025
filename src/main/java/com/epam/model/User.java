package com.epam.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class User {
    protected UUID userId;
    protected String firstName;
    protected String lastName;
    protected String userName;
    protected String password;
    protected boolean isActive;
}
