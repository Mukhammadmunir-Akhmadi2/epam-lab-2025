package com.epam.infrastructure.security.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LoginAttempt {
    private final String username;
    private int attempts;
    private LocalDateTime banStartTime;
}
