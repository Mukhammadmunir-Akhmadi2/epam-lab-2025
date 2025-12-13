package com.epam.infrastructure.security;

import com.epam.application.provider.BruteForceProtector;
import com.epam.infrastructure.security.model.LoginAttempt;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class BruteForceProtectorImpl implements BruteForceProtector {

    private final static Logger LOGGER = LoggerFactory.getLogger(BruteForceProtectorImpl.class);

    @Value("${app.security.brute-force-protection.max-attempts}")
    private int maxAttempts;

    @Value("${app.security.brute-force-protection.ban-duration-minutes}")
    private int banDurationMinutes;

    @Value("${app.security.brute-force-protection.file}")
    private String storagePath;

    private final Map<String, LoginAttempt> attempts = new ConcurrentHashMap<>();

    @Override
    public void recordFailedAttempt(String username) {
        LoginAttempt attempt = attempts.getOrDefault(username, new LoginAttempt(username));
        attempt.setAttempts(attempt.getAttempts() + 1);

        if (attempt.getAttempts() >= maxAttempts && attempt.getBanStartTime() == null) {
            attempt.setBanStartTime(LocalDateTime.now());
            LOGGER.warn("User '{}' has been blocked due to too many failed login attempts", username);
        }

        attempts.put(username, attempt);
    }

    @Override
    public void resetAttempts(String username) {
        attempts.remove(username);
    }

    @Override
    public boolean isBlocked(String username) {
        LoginAttempt attempt = attempts.get(username);
        if (attempt == null || attempt.getAttempts() < maxAttempts || attempt.getBanStartTime() == null) {
            return false;
        }

        LocalDateTime banEnd = attempt.getBanStartTime().plusMinutes(banDurationMinutes);
        return LocalDateTime.now().isBefore(banEnd);
    }

    @Override
    public long getRemainingBlockMinutes(String username) {
        LoginAttempt attempt = attempts.get(username);
        if (attempt == null || attempt.getAttempts() < maxAttempts || attempt.getBanStartTime() == null) {
            return 0;
        }

        LocalDateTime banEnd = attempt.getBanStartTime().plusMinutes(banDurationMinutes);
        if (LocalDateTime.now().isAfter(banEnd)) {
            return 0;
        }

        return Duration.between(LocalDateTime.now(), banEnd).toMinutes();
    }

    @PostConstruct
    private synchronized void load() {
        try {
            File storage = new File(storagePath);
            if (!storage.exists()) return;
            ObjectMapper mapper = new ObjectMapper();
            Map<String, LoginAttempt> loaded = mapper.readValue(storage, new TypeReference<>() {});
            attempts.putAll(loaded);
            LOGGER.info("BruteForceProtectorService loaded {} records from storage", loaded.size());
        } catch (Exception ex) {
            LOGGER.warn("BruteForceProtectorService failed to load records from storage");
            LOGGER.debug("Stack trace: ", ex);
        }
    }

    @PreDestroy
    private synchronized void save() {
        try {
            File storage = new File(storagePath);

            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(storage, attempts);

            LOGGER.info("BruteForceProtectorService saved {} records to storage", attempts.size());
        } catch (Exception ex) {
            LOGGER.warn("BruteForceProtectorService failed to save records to storage");
            LOGGER.debug("Stack trace: ", ex);
        }
    }
}
