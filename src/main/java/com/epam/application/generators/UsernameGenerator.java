package com.epam.application.generators;

import com.epam.model.User;

import java.util.function.Predicate;

public interface UsernameGenerator {
    String generateUsername(User user, Predicate<String> usernameExists);
}
