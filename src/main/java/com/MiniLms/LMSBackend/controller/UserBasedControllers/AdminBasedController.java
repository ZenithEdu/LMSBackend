package com.MiniLms.LMSBackend.controller.UserBasedControllers;

import com.MiniLms.LMSBackend.dto.ResponseDTO.RegistrationAndLoginResponseDTOS.EmployeeRegistrationResponseDTO;
import com.MiniLms.LMSBackend.dto.ResponseDTO.RegistrationAndLoginResponseDTOS.UserRegistrationResponseDTO;
import com.MiniLms.LMSBackend.service.BatchService.IBatchService;
import com.MiniLms.LMSBackend.service.ContentService.ISubjectService;
import com.MiniLms.LMSBackend.service.UserService.IEmployeeService;
import com.MiniLms.LMSBackend.service.UserService.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminBasedController {

    private final IBatchService batchService;
    private final IUserService userService;
    public final ISubjectService subjectService;
    public final IEmployeeService employeeService;


    @Autowired
    public AdminBasedController(
        IBatchService batchService,
        IUserService userService,
        ISubjectService subjectService,
        IEmployeeService employeeService
    ){
        this.batchService = batchService;
        this.userService = userService;
        this.subjectService = subjectService;
        this.employeeService = employeeService;
    }

    @GetMapping("/batchCount")
    public ResponseEntity<?> getBatchCount(){
        return ResponseEntity.ok(batchService.getBatchCount());
    }

    @GetMapping("/studentCount")
    public ResponseEntity<?> getStudentCount(){
        return ResponseEntity.ok(userService.studentCount());
    }

    @GetMapping("/subjectCount")
    public ResponseEntity<?> getSubjectCount(){
        return ResponseEntity.ok(subjectService.subjectCount());
    }

    @DeleteMapping("/deleteEmployee/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable String id){
        userService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{filterRole}")
    public ResponseEntity<List<UserRegistrationResponseDTO>> filterByRole(@PathVariable String filterRole){
        List<UserRegistrationResponseDTO> result = userService.findAllByRole(filterRole);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/allEmployee")
    public ResponseEntity<List<EmployeeRegistrationResponseDTO>> getAllEmployee(){
        List<EmployeeRegistrationResponseDTO> response = employeeService.getAllEmployees();
        return ResponseEntity.ok(response);
    }
}
