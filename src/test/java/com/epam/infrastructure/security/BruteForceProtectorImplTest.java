package com.epam.infrastructure.security;

import com.epam.infrastructure.security.model.LoginAttempt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class BruteForceProtectorImplTest {

    private BruteForceProtectorImpl protector;

    @BeforeEach
    void setUp() {
        protector = new BruteForceProtectorImpl();

        ReflectionTestUtils.setField(protector, "maxAttempts", 3);
        ReflectionTestUtils.setField(protector, "banDurationMinutes", 5);
        ReflectionTestUtils.setField(protector, "storagePath", "target/brute-force-test.json");
    }

    @Test
    void recordFailedAttempt_ShouldIncrementAttempts() {
        protector.recordFailedAttempt("john");
        protector.recordFailedAttempt("john");

        assertThat(protector.isBlocked("john")).isFalse();

        protector.recordFailedAttempt("john");

        assertThat(protector.isBlocked("john")).isTrue();
    }

    @Test
    void isBlocked_ShouldReturnFalse_WhenUnderLimit() {
        protector.recordFailedAttempt("alice");
        assertThat(protector.isBlocked("alice")).isFalse();
    }

    @Test
    void isBlocked_ShouldReturnTrue_WhenMaxAttemptsReached() {
        for (int i = 0; i < 3; i++) {
            protector.recordFailedAttempt("bob");
        }
        assertThat(protector.isBlocked("bob")).isTrue();
    }

    @Test
    void getRemainingBlockMinutes_ShouldReturnCorrectTime() throws InterruptedException {
        for (int i = 0; i < 3; i++) {
            protector.recordFailedAttempt("charlie");
        }

        long remaining = protector.getRemainingBlockMinutes("charlie");
        assertThat(remaining).isBetween(0L, 5L);
    }

    @Test
    void resetAttempts_ShouldUnblockUser() {
        for (int i = 0; i < 3; i++) {
            protector.recordFailedAttempt("dave");
        }
        assertThat(protector.isBlocked("dave")).isTrue();

        protector.resetAttempts("dave");
        assertThat(protector.isBlocked("dave")).isFalse();
    }

    @Test
    void isBlocked_ShouldReturnFalse_WhenBanExpired() {
        LoginAttempt attempt = new LoginAttempt("eve");
        attempt.setAttempts(3);
        attempt.setBanStartTime(LocalDateTime.now().minusMinutes(10));
        protector.recordFailedAttempt("eve");
        protector.resetAttempts("eve");
        ReflectionTestUtils.setField(protector, "attempts", java.util.Map.of("eve", attempt));

        assertThat(protector.isBlocked("eve")).isFalse();
    }
}
