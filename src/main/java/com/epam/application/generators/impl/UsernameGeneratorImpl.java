package com.epam.application.generators.impl;

import com.epam.application.generators.UsernameGenerator;
import com.epam.model.User;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;

@Component
public class UsernameGeneratorImpl implements UsernameGenerator {
    @Override
    public String generateUsername(User user, Predicate<String> usernameExists) {
        String base = normalize(user.getFirstName()) + "." + normalize(user.getLastName());
        int counter = 0;
        String candidate = base;

        while (usernameExists.test(candidate)) {
            candidate = base + ++counter;
        }
        return candidate;
    }

    private String normalize(String s) {
        return s.trim()
                .replaceAll("\\s+", "");
    }
}
