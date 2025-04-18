package com.MiniLms.LMSBackend.service.securityService;

import com.MiniLms.LMSBackend.model.UserModelAndSubModels.EmployeeModel;
import com.MiniLms.LMSBackend.model.UserModelAndSubModels.StudentModel;
import com.MiniLms.LMSBackend.repository.UserRepositories.IEmployeeRepository;
import com.MiniLms.LMSBackend.repository.UserRepositories.IStudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final IStudentRepository studentRepository;
    private final IEmployeeRepository employeeRepository;

    @Autowired
    public CustomUserDetailsService(IStudentRepository studentRepository,
                                    IEmployeeRepository employeeRepository) {
        this.studentRepository = studentRepository;
        this.employeeRepository = employeeRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<StudentModel> studentOptional = studentRepository.findByEmail(email);
        if (studentOptional.isPresent()) {
            return new UserPrincipal(studentOptional.get());
        }
        Optional<EmployeeModel> employeeOptional = employeeRepository.findByEmail(email);
        if (employeeOptional.isPresent()) {
            return new UserPrincipal(employeeOptional.get());
        }
        throw new UsernameNotFoundException("User not found with email: " + email);
    }
}
