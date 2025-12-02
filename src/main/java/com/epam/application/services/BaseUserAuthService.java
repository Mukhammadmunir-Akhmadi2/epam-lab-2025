package com.epam.application.services;

import jakarta.validation.constraints.Size;

public interface BaseUserAuthService {
    boolean toggleActive(String username);
    void changePassword(String username, String oldPassword, @Size(min = 6) String newPassword);
    String authenticateUser(String username, String password);
}
