package com.MiniLms.LMSBackend.service.PasswordResetService;

import com.MiniLms.LMSBackend.dto.ResponseDTO.RegistrationAndLoginResponseDTOS.MessageResultResponseDTO;
import com.MiniLms.LMSBackend.service.RegistrationService.IGenerateResetToken;

public interface IPasswordTokenMail extends IGenerateResetToken {
    public MessageResultResponseDTO generateTokenForAnEmailAndSendEmail(String email);
}
