package com.epam.application.generators;

import com.epam.application.generators.impl.PasswordGeneratorImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordGeneratorImplTest {

    private final PasswordGeneratorImpl generator = new PasswordGeneratorImpl();

    @Test
    void shouldGeneratePasswordOfCorrectLength() {
        String password = generator.generatePassword(12);
        assertNotNull(password);
        assertEquals(12, password.length());
    }

    @Test
    void shouldOnlyContainValidCharacters() {
        String password = generator.generatePassword(100);
        assertTrue(password.matches("[A-Za-z0-9$%&@#]+"));
    }

    @Test
    void shouldGenerateDifferentPasswords() {
        String p1 = generator.generatePassword(10);
        String p2 = generator.generatePassword(10);
        assertNotEquals(p1, p2);
    }
}