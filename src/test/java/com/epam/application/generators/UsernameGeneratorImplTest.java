package com.epam.application.generators;

import com.epam.application.generators.impl.UsernameGeneratorImpl;
import com.epam.model.Trainee;
import com.epam.model.User;
import org.junit.jupiter.api.Test;

import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

class UsernameGeneratorImplTest {

    private final UsernameGeneratorImpl generator = new UsernameGeneratorImpl();

    @Test
    void shouldGenerateNormalizedUsername() {
        User user = new Trainee();
        user.setFirstName(" John ");
        user.setLastName(" Doe ");
        String username = generator.generateUsername(user, u -> false);
        assertEquals("John.Doe", username);
    }

    @Test
    void shouldAppendCounterIfExists() {
        User user = new Trainee();
        user.setFirstName("John");
        user.setLastName("Doe");

        Predicate<String> exists = u -> u.equals("John.Doe") || u.equals("John.Doe1");
        String username = generator.generateUsername(user, exists);
        assertEquals("John.Doe2", username);
    }

    @Test
    void shouldHandleEmptyNames() {
        User user = new Trainee();
        user.setFirstName(" ");
        user.setLastName(" ");
        String username = generator.generateUsername(user, u -> false);
        assertEquals(".", username);
    }
}
