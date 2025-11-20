package com.epam.application.provider;

import com.epam.model.User;

public interface AuthProviderService {
    boolean isAuthenticated(String username);
    String setAuthenticatedUser(User user);
    User getAuthenticatedUser();
    void logout();
}
