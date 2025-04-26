package com.MiniLms.LMSBackend.service.securityService;

import com.MiniLms.LMSBackend.model.SecurityModels.RefreshToken;
import com.MiniLms.LMSBackend.repository.SecurityRepository.IRefreshTokenRepository;
import com.MiniLms.LMSBackend.service.UserService.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class TokenServiceImpl implements ITokenService{
    private final IRefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final IUserService userService;

    @Autowired
    public TokenServiceImpl(
        IRefreshTokenRepository refreshTokenRepository,
        JwtTokenProvider jwtTokenProvider,
        IUserService userService
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    @Override
    public void saveRefreshToken(String userId, String refreshToken) {
        userService.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id : " + userId));
        Date expiryDate = jwtTokenProvider.getExpirationDateFromToken(refreshToken);
        RefreshToken newToken = RefreshToken.builder()
            .token(refreshToken)
            .userId(userId)
            .expiryDate(expiryDate)
            .revoked(false)
            .build();
        refreshTokenRepository.save(newToken);
    }

    @Override
    public boolean isValidRefreshToken(String token) {
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByToken(token);
        if(refreshToken.isPresent()){
            if(!refreshToken.get().isRevoked() && refreshToken.get().getExpiryDate().after(new Date())){
                return true;
            }else{
                return false;
            }
        }
        return false;
    }

    @Override
    public void revokeRefreshToken(String token) {
        refreshTokenRepository.findByToken(token)
            .ifPresent(refreshToken -> {
                refreshToken.setRevoked(true);
                refreshTokenRepository.save(refreshToken);
            });
    }

    @Override
    public void revokeAllUserTokens(String userId) {
        refreshTokenRepository.deleteAllByUserId(userId);
    }
}
