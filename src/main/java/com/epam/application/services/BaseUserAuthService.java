package com.epam.application.services;

public interface BaseUserAuthService {
    boolean toggleActive(String username);
    void changePassword(String username, String oldPassword, String newPassword);
    String authenticateUser(String username, String password);
}
