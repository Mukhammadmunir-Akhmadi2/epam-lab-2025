package com.epam.application.repository;

import com.epam.model.User;

import java.util.Optional;

public interface BaseUserRepository {
    Optional<User> findByUserName(String username);
    User save(User user);
}
