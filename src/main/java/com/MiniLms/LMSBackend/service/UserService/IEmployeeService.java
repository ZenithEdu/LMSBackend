package com.MiniLms.LMSBackend.service.UserService;

import com.MiniLms.LMSBackend.dto.RequestDTO.RegistrationAndLoginRequestDTOS.EmployeeRegistrationRequestDTO;
import com.MiniLms.LMSBackend.dto.ResponseDTO.RegistrationAndLoginResponseDTOS.EmployeeRegistrationResponseDTO;

public interface IEmployeeService {
    EmployeeRegistrationResponseDTO updateEmployee(String id,EmployeeRegistrationRequestDTO employeeRegistrationRequestDTO);
}
