package com.MiniLms.LMSBackend.controller.UserBasedControllers;

import com.MiniLms.LMSBackend.dto.RequestDTO.RegistrationAndLoginRequestDTOS.EmployeeRegistrationRequestDTO;
import com.MiniLms.LMSBackend.dto.ResponseDTO.RegistrationAndLoginResponseDTOS.EmployeeRegistrationResponseDTO;
import com.MiniLms.LMSBackend.service.UserService.IEmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employee")
public class EmployeeBasedController {

    private final IEmployeeService employeeService;


    @Autowired
    public EmployeeBasedController(
        IEmployeeService employeeService
    ){
        this.employeeService = employeeService;
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeRegistrationResponseDTO> updateEmployee(@PathVariable String id, @RequestBody @Valid EmployeeRegistrationRequestDTO requestDTO){
        EmployeeRegistrationResponseDTO responseDTO = employeeService.updateEmployee(id,requestDTO);
        return ResponseEntity.ok(responseDTO);
    }
}
