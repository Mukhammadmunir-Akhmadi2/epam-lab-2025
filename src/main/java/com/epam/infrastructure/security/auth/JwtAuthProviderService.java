package com.epam.infrastructure.security.auth;

import com.epam.application.provider.AuthProviderService;
import com.epam.infrastructure.security.jwt.JwtService;
import com.epam.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtAuthProviderService implements AuthProviderService {

    private final JwtService jwtService;

    @Override
    public boolean isAuthenticated(String username) {
        Authentication authentication = getAuth();
        if (authentication != null && authentication.isAuthenticated()) {
            User user = (User) authentication.getPrincipal();
            return user != null && user.getUsername().equals(username);
        }
        return false;
    }

    @Override
    public String generateTokenForUser(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        claims.put("fullName", user.getFirstName() + " " + user.getLastName());
        return jwtService.generateToken(user, claims);
    }

    @Override
    public User getAuthenticatedUser() {
        Authentication authentication = getAuth();
        if (authentication != null && authentication.isAuthenticated()) {
            return (User) authentication.getPrincipal();
        }
        return null;
    }

    @Override
    public void logout() {
        SecurityContextHolder.clearContext();
    }

    private Authentication getAuth() {
        SecurityContext context = SecurityContextHolder.getContext();
        return context.getAuthentication();
    }
}
