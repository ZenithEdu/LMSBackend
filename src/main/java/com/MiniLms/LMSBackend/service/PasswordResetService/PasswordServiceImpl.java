package com.MiniLms.LMSBackend.service.PasswordResetService;

import com.MiniLms.LMSBackend.exceptions.InvalidTokenException;
import com.MiniLms.LMSBackend.exceptions.PasswordMismatchException;
import com.MiniLms.LMSBackend.repository.IEmployeeRepository;
import com.MiniLms.LMSBackend.repository.IStudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

public abstract class PasswordServiceImpl implements IPasswordResetService{
    protected IEmployeeRepository employeeRepository;
    protected IStudentRepository studentRepository;
    protected BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public PasswordServiceImpl(
        IEmployeeRepository employeeRepository,
        IStudentRepository studentRepository,
        BCryptPasswordEncoder bCryptPasswordEncoder
    ){
        this.employeeRepository = employeeRepository;
        this.studentRepository = studentRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }
    protected void validateToken(LocalDateTime tokenExpiry){
        if(tokenExpiry == null || tokenExpiry.isBefore(LocalDateTime.now())){
            throw new InvalidTokenException("Reset token has been expired");
        }
    }
    protected void validateOldPassword(String rawOldPassword,String encodedPassword){
        if(!bCryptPasswordEncoder.matches(rawOldPassword,encodedPassword)){
            throw new PasswordMismatchException("Old password does not match");
        }
    }
}
