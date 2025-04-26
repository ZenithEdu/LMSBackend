package com.MiniLms.LMSBackend.service.securityService;

public interface ITokenService {
    void saveRefreshToken(String userId, String refreshToken);
    boolean isValidRefreshToken(String token);
    void revokeRefreshToken(String token);
    void revokeAllUserTokens(String userId);
}
