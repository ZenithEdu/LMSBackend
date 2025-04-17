package com.MiniLms.LMSBackend.service.PasswordResetService;

import com.MiniLms.LMSBackend.dto.RequestDTO.ResetPasswordRequestDTO;
import com.MiniLms.LMSBackend.dto.ResponseDTO.ResetPasswordResponseDTO;
import com.MiniLms.LMSBackend.exceptions.InvalidTokenException;
import com.MiniLms.LMSBackend.exceptions.PasswordMismatchException;
import com.MiniLms.LMSBackend.model.EmployeeModel;
import com.MiniLms.LMSBackend.model.StudentModel;
import com.MiniLms.LMSBackend.model.UserModel;
import com.MiniLms.LMSBackend.model.UserType;
import com.MiniLms.LMSBackend.repository.IEmployeeRepository;
import com.MiniLms.LMSBackend.repository.IStudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PasswordResetServiceImpl implements IPasswordResetService{
    private final IEmployeeRepository employeeRepository;
    private final IStudentRepository studentRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public PasswordResetServiceImpl(
        IEmployeeRepository employeeRepository,
        IStudentRepository studentRepository,
        BCryptPasswordEncoder bCryptPasswordEncoder
    ){
        this.employeeRepository = employeeRepository;
        this.studentRepository = studentRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public ResetPasswordResponseDTO resetPassword(ResetPasswordRequestDTO requestDTO) {
        Optional<EmployeeModel> employeeOptional = employeeRepository.findByResetToken(requestDTO.getToken());
        if(employeeOptional.isPresent()){
            return handlePasswordReset(employeeOptional.get(),requestDTO,UserType.EMPLOYEE);
        }
        Optional<StudentModel> studentOptional = studentRepository.findByResetToken(requestDTO.getToken());
        if(studentOptional.isPresent()){
            return handlePasswordReset(studentOptional.get(),requestDTO,UserType.STUDENT);
        }
        throw new InvalidTokenException("Invalid or expired reset  token");
    }
    private ResetPasswordResponseDTO handlePasswordReset(UserModel userModel, ResetPasswordRequestDTO resetPasswordRequestDTO, UserType type){
        validateToken(userModel.getTokenExpiry());
        validateOldPassword(resetPasswordRequestDTO.getOldPassword(),userModel.getPassword());

        userModel.setPassword(bCryptPasswordEncoder.encode(resetPasswordRequestDTO.getNewPassword()));
        userModel.setResetToken(null);
        userModel.setTokenExpiry(null);

        if(type.equals(UserType.EMPLOYEE)){
            employeeRepository.save((EmployeeModel) userModel);
        }else{
            studentRepository.save((StudentModel) userModel);
        }
        return new ResetPasswordResponseDTO("Password reset successful",true);
    }
    private void validateToken(LocalDateTime tokenExpiry){
        if(tokenExpiry == null || tokenExpiry.isBefore(LocalDateTime.now())){
            throw new InvalidTokenException("Reset token has been expired");
        }
    }
    private void validateOldPassword(String rawOldPassword,String encodedPassword){
        if(!bCryptPasswordEncoder.matches(rawOldPassword,encodedPassword)){
            throw new PasswordMismatchException("Old password does not match");
        }
    }
}
