package com.MiniLms.LMSBackend.service.securityService;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private int jwtExpiration;

    @Getter
    @Value("${app.jwt.refreshExpiration}")
    private int refreshExpiration;

    private final TokenBlackList tokenBlackList;

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Autowired
    public JwtTokenProvider(
        TokenBlackList tokenBlackList
    ){
        this.tokenBlackList = tokenBlackList;
    }

    public String generateAccessToken(UserPrincipal userPrincipal){
        Map<String,Object> claims = new HashMap<>();
        claims.put("id",userPrincipal.getId());
        claims.put("name", userPrincipal.getName());
        claims.put("email", userPrincipal.getEmail());
        claims.put("role", userPrincipal.getRole().name());
        claims.put("userType", userPrincipal.getUserType());

        return Jwts.builder()
            .claims().add(claims)
            .subject(userPrincipal.getEmail())
            .issuer("LMSAuth")
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis()+jwtExpiration))
            .and()
            .signWith(generateKey())
            .compact();
    }

    public String generateRefreshToken(UserPrincipal userPrincipal) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", userPrincipal.getId());
        claims.put("tokenType", "refresh");

        return Jwts.builder()
            .claims().add(claims)
            .subject(userPrincipal.getEmail())
            .issuer("LMSAuth")
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + refreshExpiration))
            .and()
            .signWith(generateKey())
            .compact();
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private SecretKey generateKey() {
        try {
            byte[] decode = Decoders.BASE64.decode(jwtSecret);
            return Keys.hmacShaKeyFor(decode);
        } catch (IllegalArgumentException e) {
            logger.error("Error generating JWT key: {}", e.getMessage());
            throw new JwtInitializationException("Failed to generate JWT signing key", e);
        }
    }
    public boolean validateToken(String token) {
        return validateToken(token, true); // Default check blacklist
    }
    public boolean validateToken(String token, boolean checkBlackList) {
        try {
            Jwts.parser()
                .verifyWith(generateKey())
                .build()
                .parseSignedClaims(token);

            if (checkBlackList && tokenBlackList.isBlackListed(token)) {
                logger.warn("JWT token is blacklisted: {}", token);
                return false;
            }
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.warn("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        } catch (JwtException e) {
            logger.error("JWT validation error: {}", e.getMessage());
        }
        return false;
    }

    public String getEmailFromToken(String token) {
        try {
            return Jwts.parser()
                .verifyWith(generateKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            logger.error("Error extracting email from token: {}", e.getMessage());
            return null;
        }
    }

    public Date getExpirationDateFromToken(String token) {
        try {
            return Jwts.parser()
                .verifyWith(generateKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();
        } catch (JwtException | IllegalArgumentException e) {
            logger.error("Error getting expiration date from token: {}", e.getMessage());
            return null;
        }
    }

    private static class JwtInitializationException extends RuntimeException {
        public JwtInitializationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
