package com.MiniLms.LMSBackend.controller;

import com.MiniLms.LMSBackend.dto.RequestDTO.EmployeeRegistrationRequestDTO;
import com.MiniLms.LMSBackend.dto.RequestDTO.StudentRegistrationRequestDTO;
import com.MiniLms.LMSBackend.dto.ResponseDTO.EmployeeRegistrationResponseDTO;
import com.MiniLms.LMSBackend.dto.ResponseDTO.StudentRegistrationResponseDTO;
import com.MiniLms.LMSBackend.model.UserType;
import com.MiniLms.LMSBackend.service.IRegistrationService;
import com.MiniLms.LMSBackend.service.serviceImpl.registrationImpl.RegistrationServiceFactory;
import com.MiniLms.LMSBackend.service.serviceImpl.registrationImpl.StudentRegistrationServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;

@RestController
public class TestController {



    private final RegistrationServiceFactory registrationServiceFactory;

    @Autowired
    public TestController(RegistrationServiceFactory registrationServiceFactory){
        this.registrationServiceFactory = registrationServiceFactory;
    }

    public IRegistrationService registrationService;

    @PostMapping("/empReg")
    public ResponseEntity<?> postEmployee(@Valid @RequestBody EmployeeRegistrationRequestDTO employeeRegistrationRequestDto){
        registrationService = registrationServiceFactory.getService(UserType.EMPLOYEE);
        EmployeeRegistrationResponseDTO responseDTO = (EmployeeRegistrationResponseDTO) registrationService.register(employeeRegistrationRequestDto);
        return ResponseEntity.ok(responseDTO);
    }
    @PostMapping("/stuReg")
    public ResponseEntity<?> postStudent(@Valid @RequestBody StudentRegistrationRequestDTO studentRegistrationRequestDTO){
        registrationService = registrationServiceFactory.getService(UserType.STUDENT);
        StudentRegistrationResponseDTO responseDTO = (StudentRegistrationResponseDTO) registrationService.register(studentRegistrationRequestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/bulk-register-students")
    public ResponseEntity<?> bulkRegisterStudents(@RequestBody @Valid List<StudentRegistrationRequestDTO> studentDtos) {
        registrationService = registrationServiceFactory.getService(UserType.STUDENT);
        List<StudentRegistrationResponseDTO> resposeDtos = ((StudentRegistrationServiceImpl)registrationService).registerMultipleStudents(studentDtos);
        return ResponseEntity.ok(resposeDtos);
    }
}
