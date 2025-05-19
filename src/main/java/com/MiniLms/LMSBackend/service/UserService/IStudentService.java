package com.MiniLms.LMSBackend.service.UserService;

import com.MiniLms.LMSBackend.dto.ResponseDTO.RegistrationAndLoginResponseDTOS.StudentRegistrationResponseDTO;

public interface IStudentService {
    StudentRegistrationResponseDTO findStudentById(String id);
}