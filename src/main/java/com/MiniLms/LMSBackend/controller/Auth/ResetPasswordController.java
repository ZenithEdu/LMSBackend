package com.MiniLms.LMSBackend.controller.Auth;

import com.MiniLms.LMSBackend.dto.RequestDTO.ForgotPasswordRequestDTO;
import com.MiniLms.LMSBackend.dto.RequestDTO.ResetPasswordFirstTimeRequestDTO;
import com.MiniLms.LMSBackend.dto.RequestDTO.VerifyEmailRequestDTO;
import com.MiniLms.LMSBackend.dto.ResponseDTO.MessageResultResponseDTO;
import com.MiniLms.LMSBackend.service.PasswordResetService.*;
import com.MiniLms.LMSBackend.service.UserService.IUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class ResetPasswordController {
    private final PasswordChangeServiceFactory passwordResetServiceFactory;
    private final IUserService userService;
    private IPasswordResetService passwordResetService;
    private final IPasswordTokenMail passwordTokenMail;
    @Autowired
    public ResetPasswordController(
        PasswordChangeServiceFactory passwordResetServiceFactory,
        IUserService userService,
        IPasswordTokenMail passwordTokenMail
    ){
        this.passwordResetServiceFactory = passwordResetServiceFactory;
        this.userService = userService;
        this.passwordTokenMail = passwordTokenMail;
    }

    @PostMapping("/reset/first-time")
    public ResponseEntity<?> resetPassword(@RequestBody @Valid ResetPasswordFirstTimeRequestDTO resetPasswordRequestDTO){
        passwordResetService = passwordResetServiceFactory.getService(PasswordChange.REGISTRATION);
        MessageResultResponseDTO responseDTO = passwordResetService.resetPassword(resetPasswordRequestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/reset/verify-email")
    public ResponseEntity<?> verifyMailandSendToken(@RequestBody @Valid VerifyEmailRequestDTO verifyEmailRequestDTO){
        MessageResultResponseDTO responseDTO = passwordTokenMail.generateTokenForAnEmailAndSendEmail(verifyEmailRequestDTO.getEmail());
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/reset/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody @Valid ForgotPasswordRequestDTO forgotPasswordRequestDTO){
        passwordResetService = passwordResetServiceFactory.getService(PasswordChange.FORGOT_PASSWORD);
        MessageResultResponseDTO responseDTO = passwordResetService.resetPassword(forgotPasswordRequestDTO);
        return ResponseEntity.ok(responseDTO);
    }
}
