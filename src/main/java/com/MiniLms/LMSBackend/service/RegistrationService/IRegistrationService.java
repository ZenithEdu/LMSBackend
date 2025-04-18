package com.MiniLms.LMSBackend.service.RegistrationService;

import com.MiniLms.LMSBackend.dto.ResponseDTO.RegistrationAndLoginResponseDTOS.UserRegistrationResponseDTO;
import com.MiniLms.LMSBackend.dto.RequestDTO.RegistrationAndLoginRequestDTOS.UserRegistrationRequestDTO;

public interface IRegistrationService {
    UserRegistrationResponseDTO register(UserRegistrationRequestDTO userRegistrationRequestDTO) throws RuntimeException;
}
