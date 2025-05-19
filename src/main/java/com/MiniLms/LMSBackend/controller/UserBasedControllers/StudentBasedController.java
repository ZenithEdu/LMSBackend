package com.MiniLms.LMSBackend.controller.UserBasedControllers;

import com.MiniLms.LMSBackend.dto.ResponseDTO.RegistrationAndLoginResponseDTOS.StudentRegistrationResponseDTO;
import com.MiniLms.LMSBackend.service.UserService.IStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/student")
public class StudentBasedController {
    private final IStudentService studentService;


    @Autowired
    public StudentBasedController(
        IStudentService studentService
    ){
        this.studentService = studentService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentRegistrationResponseDTO> getStudentById(@PathVariable String id){
        return ResponseEntity.ok(studentService.findStudentById(id));
    }
}
