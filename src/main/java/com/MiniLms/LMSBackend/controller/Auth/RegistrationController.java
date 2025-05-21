package com.MiniLms.LMSBackend.controller.Auth;

import com.MiniLms.LMSBackend.dto.RequestDTO.RegistrationAndLoginRequestDTOS.EmployeeRegistrationRequestDTO;
import com.MiniLms.LMSBackend.dto.RequestDTO.RegistrationAndLoginRequestDTOS.StudentRegistrationRequestDTO;
import com.MiniLms.LMSBackend.dto.ResponseDTO.RegistrationAndLoginResponseDTOS.EmployeeRegistrationResponseDTO;
import com.MiniLms.LMSBackend.dto.ResponseDTO.RegistrationAndLoginResponseDTOS.StudentRegistrationResponseDTO;
import com.MiniLms.LMSBackend.model.UserModelAndSubModels.UserType;
import com.MiniLms.LMSBackend.service.RegistrationService.IRegistrationService;
import com.MiniLms.LMSBackend.service.RegistrationService.RegistrationServiceFactory;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/auth")
public class RegistrationController {
    private final RegistrationServiceFactory registrationServiceFactory;

    @Autowired
    public RegistrationController(
        RegistrationServiceFactory registrationServiceFactory
    ){
        this.registrationServiceFactory = registrationServiceFactory;
    }

    @PostMapping("/register/employee")
    public ResponseEntity<EmployeeRegistrationResponseDTO> registerEmployee(
        @Valid @RequestBody EmployeeRegistrationRequestDTO request) {
        IRegistrationService registrationService = registrationServiceFactory.getService(UserType.EMPLOYEE);
        EmployeeRegistrationResponseDTO response = (EmployeeRegistrationResponseDTO) registrationService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/register/student")
    public ResponseEntity<StudentRegistrationResponseDTO> registerStudent(
        @Valid @RequestBody StudentRegistrationRequestDTO request){
        IRegistrationService registrationService = registrationServiceFactory.getService(UserType.STUDENT);
        StudentRegistrationResponseDTO responseDTO = (StudentRegistrationResponseDTO) registrationService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }
}
