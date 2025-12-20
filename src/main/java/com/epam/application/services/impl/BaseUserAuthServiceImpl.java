package com.epam.application.services.impl;

import com.epam.application.exceptions.ResourceNotFoundException;
import com.epam.application.provider.AuthProviderService;
import com.epam.application.provider.BruteForceProtector;
import com.epam.application.repository.BaseUserRepository;
import com.epam.application.services.BaseUserAuthService;
import com.epam.model.User;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class BaseUserAuthServiceImpl implements BaseUserAuthService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseUserAuthServiceImpl.class);

    private final AuthProviderService authProviderService;
    private final BruteForceProtector bruteForceProtector;
    private final AuthenticationManager authenticationManager;
    private final BaseUserRepository baseUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean toggleActive(String username) {
        User user = baseUserRepository.findByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        user.setActive(!user.isActive());
        LOGGER.info("User '{}' has been {}", username, user.isActive() ? "activated" : "deactivated");

        baseUserRepository.save(user);
        return user.isActive();
    }

    @Override
    public void changePassword(String username, String oldPassword, @Size(min = 6) String newPassword) {
        User user = baseUserRepository.findByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException("Trainee not found with username: " + username));

        if (!user.getPassword().equals(oldPassword)) {
            throw new BadCredentialsException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        baseUserRepository.save(user);
        LOGGER.info("Password changed for trainee username={}", username);
    }

    @Override
    public String authenticateUser(String username, String password) {

        if (bruteForceProtector.isBlocked(username)) {
            long minutes = bruteForceProtector.getRemainingBlockMinutes(username);
            throw new BadCredentialsException(
                    "User is blocked. Try again in " + (minutes + 1) + " minutes"
            );        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            bruteForceProtector.resetAttempts(username);

            User user = (User) authentication.getPrincipal();
            LOGGER.info("User '{}' authenticated successfully", username);
            return authProviderService.generateTokenForUser(user);

        } catch (BadCredentialsException ex) {
            bruteForceProtector.recordFailedAttempt(username);
            LOGGER.warn("Authentication failed for user '{}': {}", username, ex.getMessage());

            if (bruteForceProtector.isBlocked(username)) {
                long minutes = bruteForceProtector.getRemainingBlockMinutes(username);
                throw new BadCredentialsException(
                        "Too many failed attempts. You are blocked for " + minutes + " minute" + (minutes > 1 ? "s" : "")
                );
            }
            throw new BadCredentialsException("Invalid username or password");
        }
    }
}
