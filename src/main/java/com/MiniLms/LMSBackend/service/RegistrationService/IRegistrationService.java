package com.MiniLms.LMSBackend.service.RegistrationService;

import com.MiniLms.LMSBackend.dto.ResponseDTO.UserRegistrationResponseDTO;
import com.MiniLms.LMSBackend.dto.RequestDTO.UserRegistrationRequestDTO;

import java.util.UUID;

public interface IRegistrationService {
    UserRegistrationResponseDTO register(UserRegistrationRequestDTO userRegistrationRequestDTO) throws RuntimeException;
}
