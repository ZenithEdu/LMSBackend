package com.MiniLms.LMSBackend.service.PasswordResetService;

import com.MiniLms.LMSBackend.dto.RequestDTO.PasswordResetRequestDTO;
import com.MiniLms.LMSBackend.dto.RequestDTO.ResetPasswordFirstTimeRequestDTO;
import com.MiniLms.LMSBackend.dto.ResponseDTO.MessageResultResponseDTO;
import com.MiniLms.LMSBackend.exceptions.InvalidTokenException;
import com.MiniLms.LMSBackend.model.EmployeeModel;
import com.MiniLms.LMSBackend.model.StudentModel;
import com.MiniLms.LMSBackend.model.UserModel;
import com.MiniLms.LMSBackend.model.UserType;
import com.MiniLms.LMSBackend.repository.IEmployeeRepository;
import com.MiniLms.LMSBackend.repository.IStudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("passwordResetFirstTimeService")
public class PasswordResetFirstTimeServiceImpl extends PasswordServiceImpl implements IPasswordResetService{

    @Autowired
    public PasswordResetFirstTimeServiceImpl(IEmployeeRepository employeeRepository, IStudentRepository studentRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        super(employeeRepository, studentRepository, bCryptPasswordEncoder);
    }

    @Override
    public MessageResultResponseDTO resetPassword(PasswordResetRequestDTO requestDTO) {
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
    private MessageResultResponseDTO handlePasswordReset(UserModel userModel, PasswordResetRequestDTO resetDTO, UserType type){
        ResetPasswordFirstTimeRequestDTO resetPasswordRequestDTO = (ResetPasswordFirstTimeRequestDTO) resetDTO;
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
        return new MessageResultResponseDTO("Password reset successful",true);
    }
}
