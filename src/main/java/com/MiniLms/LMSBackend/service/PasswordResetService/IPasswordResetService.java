package com.MiniLms.LMSBackend.service.PasswordResetService;

import com.MiniLms.LMSBackend.dto.RequestDTO.PasswordResetRequestDTO;
import com.MiniLms.LMSBackend.dto.ResponseDTO.MessageResultResponseDTO;

public interface IPasswordResetService {
    MessageResultResponseDTO resetPassword(PasswordResetRequestDTO requestDTO);
}
