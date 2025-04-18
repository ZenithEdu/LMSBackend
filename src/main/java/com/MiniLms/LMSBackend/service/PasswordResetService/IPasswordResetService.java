package com.MiniLms.LMSBackend.service.PasswordResetService;

import com.MiniLms.LMSBackend.dto.RequestDTO.RegistrationAndLoginRequestDTOS.PasswordResetRequestDTO;
import com.MiniLms.LMSBackend.dto.ResponseDTO.RegistrationAndLoginResponseDTOS.MessageResultResponseDTO;

public interface IPasswordResetService {
    MessageResultResponseDTO resetPassword(PasswordResetRequestDTO requestDTO);
}
