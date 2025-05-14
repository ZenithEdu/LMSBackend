package com.MiniLms.LMSBackend.service.UserService;

import com.MiniLms.LMSBackend.dto.RequestDTO.RegistrationAndLoginRequestDTOS.EmployeeRegistrationRequestDTO;
import com.MiniLms.LMSBackend.dto.ResponseDTO.RegistrationAndLoginResponseDTOS.EmployeeRegistrationResponseDTO;

import java.util.List;

public interface IEmployeeService {
    EmployeeRegistrationResponseDTO updateEmployee(String id,EmployeeRegistrationRequestDTO employeeRegistrationRequestDTO);
    List<EmployeeRegistrationResponseDTO>  getAllEmployees();
}
