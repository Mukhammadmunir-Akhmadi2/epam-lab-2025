package com.epam.application.provider;

import com.epam.application.exceptions.UnauthorizedAccess;
import com.epam.model.User;

public interface AuthProviderService {
    boolean isAuthenticated(String username);
    String generateTokenForUser(User user);
    User getAuthenticatedUser();
    void logout();
    default void validateCurrentUser(String username) {
        if (!isAuthenticated(username)) {
            throw new UnauthorizedAccess("User " + username + " is not authenticated.");
        }
    }
}
