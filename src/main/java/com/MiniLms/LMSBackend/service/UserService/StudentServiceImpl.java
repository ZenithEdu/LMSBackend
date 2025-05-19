package com.MiniLms.LMSBackend.service.UserService;

import com.MiniLms.LMSBackend.dto.ResponseDTO.RegistrationAndLoginResponseDTOS.StudentRegistrationResponseDTO;
import com.MiniLms.LMSBackend.exceptions.ResourceNotFoundException;
import com.MiniLms.LMSBackend.model.UserModelAndSubModels.StudentModel;
import com.MiniLms.LMSBackend.repository.UserRepositories.IStudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StudentServiceImpl implements IStudentService{

    private final IStudentRepository studentRepository;


    @Autowired
    public StudentServiceImpl (
        IStudentRepository studentRepository
    ){
        this.studentRepository = studentRepository;
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STUDENT')")
    public StudentRegistrationResponseDTO findStudentById(String id) {
        Optional<StudentModel> student = studentRepository.findById(id);
        if(student.isEmpty()){
            throw new ResourceNotFoundException("Student not found with id : " + id);
        }
        return StudentRegistrationResponseDTO.fromEntity(student.get());
    }
}
