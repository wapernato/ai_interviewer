package org.example.security;

import org.example.exception.TooManyLoginAttemptsException;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LoginRateLimiter {

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final Duration WINDOW = Duration.ofMinutes(1);
    private final Map<String, LoginAttempt> attempts = new ConcurrentHashMap<>();

    private static class LoginAttempt {
        private final int failedAttempts;
        private final Instant windowStartedAt;

        private LoginAttempt(int failedAttempts, Instant windowStartedAt) {
            this.failedAttempts = failedAttempts;
            this.windowStartedAt = windowStartedAt;
        }
    }

    private boolean isExpired(LoginAttempt attempt, Instant now) {
        return attempt.windowStartedAt.plus(WINDOW).isBefore(now);
    }

    private String buildKey(String clientIp, String email){
        return clientIp + ":" + email;
    }


    public void checkLimit(String clientIp, String email){
        String key = buildKey(clientIp, email);
        Instant now = Instant.now();

        LoginAttempt loginAttempt = attempts.get(key);

        if(loginAttempt == null){
            return;
        }

        if(isExpired(loginAttempt, now)){
            attempts.remove(key);
            return;
        }

        if (loginAttempt.failedAttempts >= MAX_FAILED_ATTEMPTS) {
            throw new TooManyLoginAttemptsException("Слишком много попыток входа. Попробуйте позже.");
        }
    }


    public void registerFailedAttempt(String clientIp, String email){
        String key = buildKey(clientIp, email);
        Instant now = Instant.now();
        attempts.compute(key,
                (currentKey, existingAttempt) -> {
                    if(existingAttempt == null) {
                        return new LoginAttempt(1, now);
                    }
                    if(isExpired(existingAttempt, now)){
                        return new LoginAttempt(1, now);
                    }
                    else{
                        return new LoginAttempt(existingAttempt.failedAttempts + 1, existingAttempt.windowStartedAt);
                    }
                });

    }

    public void resetAttempts(String clientIp, String email){
        String key = buildKey(clientIp, email);
        attempts.remove(key);
    }


}
