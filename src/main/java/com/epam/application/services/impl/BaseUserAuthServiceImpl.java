package com.epam.application.services.impl;

import com.epam.application.exceptions.InvalidCredentialException;
import com.epam.application.exceptions.ResourceNotFoundException;
import com.epam.application.provider.AuthProviderService;
import com.epam.application.repository.BaseUserRepository;
import com.epam.application.services.BaseUserAuthService;
import com.epam.model.User;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class BaseUserAuthServiceImpl implements BaseUserAuthService {
    private static final Logger logger = LoggerFactory.getLogger(BaseUserAuthServiceImpl.class);

    private final AuthProviderService authProviderService;

    private final BaseUserRepository baseUserRepository;

    @Override
    public boolean toggleActive(String username) {
        User user = baseUserRepository.findByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        user.setActive(!user.isActive());
        logger.info("User '{}' has been {}", username, user.isActive() ? "activated" : "deactivated");

        baseUserRepository.save(user);
        return user.isActive();
    }

    @Override
    public void changePassword(String username, String oldPassword, @Size(min = 6) String newPassword) {
        User user = baseUserRepository.findByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException("Trainee not found with username: " + username));

        if (!user.getPassword().equals(oldPassword)) {
            throw new InvalidCredentialException("Old password is incorrect");
        }

        user.setPassword(newPassword);
        baseUserRepository.save(user);
        logger.info("Password changed for trainee username={}", username);
    }

    @Override
    public String authenticateUser(String username, String password) {

        User user = baseUserRepository.findByUserName(username)
                .orElseThrow(() ->  new ResourceNotFoundException("User not found username=" + username));

        if (!user.getPassword().equals(password)) {
            throw new InvalidCredentialException("Invalid password for user " + username);
        }
        logger.info("User '{}' authenticated successfully", username);
        return authProviderService.setAuthenticatedUser(user);
    }
}
