package com.MiniLms.LMSBackend.controller.SetupController;

import com.MiniLms.LMSBackend.dto.RequestDTO.RegistrationAndLoginRequestDTOS.EmployeeRegistrationRequestDTO;
import com.MiniLms.LMSBackend.dto.ResponseDTO.RegistrationAndLoginResponseDTOS.UserRegistrationResponseDTO;
import com.MiniLms.LMSBackend.model.UserModelAndSubModels.UserModel;
import com.MiniLms.LMSBackend.model.UserModelAndSubModels.UserType;
import com.MiniLms.LMSBackend.service.RegistrationService.EmployeeRegistrationServiceImpl;
import com.MiniLms.LMSBackend.service.RegistrationService.IRegistrationService;
import com.MiniLms.LMSBackend.service.RegistrationService.RegistrationServiceFactory;
import com.MiniLms.LMSBackend.service.securityService.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/setup")
public class SetupController {
    @Autowired
    private CustomUserDetailsService userService;

    @Autowired
    private RegistrationServiceFactory registrationServiceFactory;

    @PostMapping("/create-first-admin")
    public ResponseEntity<?> createFirstAdmin(@RequestBody EmployeeRegistrationRequestDTO adminDto) {
        // Check if any admin already exists
        if (userService.doesAdminExist()) {
            return ResponseEntity.badRequest().body("Initial admin already exists.");
        }

        // Validate and create the first admin
        EmployeeRegistrationServiceImpl employeeRegistrationService = (EmployeeRegistrationServiceImpl)registrationServiceFactory.getService(UserType.EMPLOYEE);
        UserRegistrationResponseDTO responseDTO = employeeRegistrationService.createFirstAdmin(adminDto);
        return ResponseEntity.ok("First admin created: " + responseDTO);
    }
}
