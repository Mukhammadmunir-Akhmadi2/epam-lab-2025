package com.epam.application.generators.impl;

import com.epam.application.generators.PasswordGenerator;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class PasswordGeneratorImpl implements PasswordGenerator {
    private final String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789$%&@#";
    private final SecureRandom rnd = new SecureRandom();
    @Override
    public String generatePassword(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0 ; i < length ; i++) {
            sb.append(letters.charAt(rnd.nextInt(letters.length())));
        }
        return sb.toString();
    }
}
