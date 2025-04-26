package com.MiniLms.LMSBackend.controller.Auth;

import com.MiniLms.LMSBackend.dto.RequestDTO.RegistrationAndLoginRequestDTOS.UserLoginRequestDTO;
import com.MiniLms.LMSBackend.dto.ResponseDTO.RegistrationAndLoginResponseDTOS.TokenRefreshResponse;
import com.MiniLms.LMSBackend.dto.ResponseDTO.RegistrationAndLoginResponseDTOS.UserLoginResponseDTO;
import com.MiniLms.LMSBackend.service.securityService.*;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenBlackList tokenBlackList;
    private final ITokenService tokenService;
    private final CustomUserDetailsService customUserDetailsService;

    @Autowired
    public AuthController(
        AuthenticationManager authenticationManager,
        JwtTokenProvider jwtTokenProvider,
        TokenBlackList tokenBlackList,
        ITokenService tokenService,
        CustomUserDetailsService customUserDetailsService
    ){
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.tokenBlackList = tokenBlackList;
        this.tokenService = tokenService;
        this.customUserDetailsService = customUserDetailsService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(
        @RequestBody @Valid UserLoginRequestDTO loginRequest,
        HttpServletResponse response
    ) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
                )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

            String accessToken = jwtTokenProvider.generateAccessToken(userPrincipal);
            String refreshToken = jwtTokenProvider.generateRefreshToken(userPrincipal);

            // Save refresh token to MongoDB
            tokenService.saveRefreshToken(userPrincipal.getId(), refreshToken);

            // Set refresh token in HTTP-only cookie
            ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/api/auth/refresh-token")
                .maxAge(jwtTokenProvider.getRefreshExpiration() / 1000)
                .sameSite("Strict")
                .build();

            response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

            UserLoginResponseDTO responseDTO = new UserLoginResponseDTO();
            responseDTO.setAccessToken(accessToken);
            return ResponseEntity.ok(responseDTO);

        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(
        @CookieValue(name = "refreshToken", required = false) String refreshToken,
        HttpServletResponse response
    ) {
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token is missing");
        }

        // Validate JWT structure and expiration
        if (!jwtTokenProvider.validateToken(refreshToken,false)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }

        // Check MongoDB for token validity
        if (!tokenService.isValidRefreshToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token revoked or expired");
        }

        // Extract user details
        String email = jwtTokenProvider.getEmailFromToken(refreshToken);
        UserPrincipal userPrincipal = (UserPrincipal) customUserDetailsService.loadUserByUsername(email);

        // Generate new tokens
        String newAccessToken = jwtTokenProvider.generateAccessToken(userPrincipal);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(userPrincipal);

        // Update MongoDB tokens
        tokenService.revokeRefreshToken(refreshToken);
        tokenService.saveRefreshToken(userPrincipal.getId(), newRefreshToken);

        // Set new refresh token cookie
        ResponseCookie newCookie = ResponseCookie.from("refreshToken", newRefreshToken)
            .httpOnly(true)
            .secure(true)
            .path("/api/auth/refresh-token")
            .maxAge(jwtTokenProvider.getRefreshExpiration() / 1000)
            .sameSite("Strict")
            .build();

        response.addHeader(HttpHeaders.SET_COOKIE, newCookie.toString());

        return ResponseEntity.ok(new TokenRefreshResponse(newAccessToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
        HttpServletRequest request,
        @CookieValue(name = "refreshToken", required = false) String refreshToken
    ) {
        // Invalidate access token
        String accessToken = jwtTokenProvider.resolveToken(request);
        if (accessToken != null) {
            Date expiration = jwtTokenProvider.getExpirationDateFromToken(accessToken);
            tokenBlackList.addToBlacklist(accessToken, expiration.getTime());
        }

        // Invalidate refresh token in MongoDB
        if (refreshToken != null) {
            tokenService.revokeRefreshToken(refreshToken);
        }

        // Clear refresh token cookie
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
            .httpOnly(true)
            .secure(true)
            .path("/api/auth/refresh-token")
            .maxAge(0)
            .build();

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body("Logged out successfully");
    }

}