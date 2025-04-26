package com.MiniLms.LMSBackend.service.securityService;


import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TokenBlacklistCleanupTask {
    private final TokenBlackList tokenBlacklist;

    public TokenBlacklistCleanupTask(TokenBlackList tokenBlacklist) {
        this.tokenBlacklist = tokenBlacklist;
    }

    @Scheduled(fixedRate = 3600000) // Runs every hour
    public void cleanupExpiredTokens() {
        long now = System.currentTimeMillis();
        tokenBlacklist.getBlacklistedTokens()
            .entrySet()
            .removeIf(entry -> entry.getValue() < now);
    }
}
