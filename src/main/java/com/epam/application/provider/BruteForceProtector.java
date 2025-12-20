package com.epam.application.provider;

public interface BruteForceProtector {
    void recordFailedAttempt(String username);
    void resetAttempts(String username);
    boolean isBlocked(String username);
    long getRemainingBlockMinutes(String username);
}
