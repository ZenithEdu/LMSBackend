package com.MiniLms.LMSBackend.service.securityService;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    public String generateToken(UserPrincipal userPrincipal){
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
        try {
            Jwts.parser()
                .verifyWith(generateKey())
                .build()
                .parseSignedClaims(token);
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

    private static class JwtInitializationException extends RuntimeException {
        public JwtInitializationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
