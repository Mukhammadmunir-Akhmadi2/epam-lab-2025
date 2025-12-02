package com.epam.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    protected String userId;
    @NotBlank
    protected String firstName;
    @NotBlank
    protected String lastName;
    protected String username;
    protected String password;
    protected boolean isActive;
}
