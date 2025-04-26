package com.MiniLms.LMSBackend.service.securityService;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenBlackList {
    private final Map<String,Long> blacklistedTokens = new ConcurrentHashMap<>();

    public void addToBlacklist(String token, Long expirationTime){
        blacklistedTokens.put(token,expirationTime);
    }

    public boolean isBlackListed(String token){
        return blacklistedTokens.containsKey(token);
    }

    public Map<String,Long> getBlacklistedTokens(){
        return blacklistedTokens;
    }
}
