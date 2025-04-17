package com.MiniLms.LMSBackend.controller.Auth;

import com.MiniLms.LMSBackend.dto.RequestDTO.ResetPasswordRequestDTO;
import com.MiniLms.LMSBackend.dto.ResponseDTO.ResetPasswordResponseDTO;
import com.MiniLms.LMSBackend.service.PasswordResetService.IPasswordResetService;
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
    private final IPasswordResetService passwordResetService;

    @Autowired
    public ResetPasswordController(IPasswordResetService passwordResetService){
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestBody @Valid ResetPasswordRequestDTO resetPasswordRequestDTO){
        ResetPasswordResponseDTO responseDTO = passwordResetService.resetPassword(resetPasswordRequestDTO);
        return ResponseEntity.ok(responseDTO);
    }


}
