package com.MiniLms.LMSBackend.service.PasswordResetService;

import com.MiniLms.LMSBackend.dto.RequestDTO.ResetPasswordRequestDTO;
import com.MiniLms.LMSBackend.dto.ResponseDTO.ResetPasswordResponseDTO;

public interface IPasswordResetService {
    ResetPasswordResponseDTO resetPassword(ResetPasswordRequestDTO requestDTO);
}
