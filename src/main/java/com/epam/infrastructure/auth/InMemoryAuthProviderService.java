package com.epam.infrastructure.auth;

import com.epam.application.provider.AuthProviderService;
import com.epam.model.User;
import org.springframework.stereotype.Component;

@Component
public class InMemoryAuthProviderService implements AuthProviderService {


    private User authenticatedUser;

    @Override
    public boolean isAuthenticated(String username) {
        return authenticatedUser != null && authenticatedUser.getUsername().equals(username);
    }

    @Override
    public String setAuthenticatedUser(User user) {
        this.authenticatedUser = user;
        return "User " + user.getUsername() + " authenticated successfully.";

    }

    @Override
    public User getAuthenticatedUser() {
        return authenticatedUser;
    }

    @Override
    public void logout() {
        authenticatedUser = null;
    }
}
