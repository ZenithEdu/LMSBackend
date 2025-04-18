package com.MiniLms.LMSBackend.service.PasswordResetService;

import com.MiniLms.LMSBackend.dto.RequestDTO.RegistrationAndLoginRequestDTOS.ForgotPasswordRequestDTO;
import com.MiniLms.LMSBackend.dto.RequestDTO.RegistrationAndLoginRequestDTOS.PasswordResetRequestDTO;
import com.MiniLms.LMSBackend.dto.ResponseDTO.RegistrationAndLoginResponseDTOS.MessageResultResponseDTO;
import com.MiniLms.LMSBackend.exceptions.InvalidEmailException;
import com.MiniLms.LMSBackend.exceptions.InvalidTokenException;
import com.MiniLms.LMSBackend.exceptions.PasswordMismatchException;
import com.MiniLms.LMSBackend.model.UserModelAndSubModels.EmployeeModel;
import com.MiniLms.LMSBackend.model.UserModelAndSubModels.StudentModel;
import com.MiniLms.LMSBackend.model.UserModelAndSubModels.UserModel;
import com.MiniLms.LMSBackend.model.UserModelAndSubModels.UserType;
import com.MiniLms.LMSBackend.repository.UserRepositories.IEmployeeRepository;
import com.MiniLms.LMSBackend.repository.UserRepositories.IStudentRepository;
import com.MiniLms.LMSBackend.service.UserService.IUserService;
import com.MiniLms.LMSBackend.service.emailService.IForgotPasswordEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service("forgotPasswordService")
public class ForgotPasswordServiceImpl extends PasswordServiceImpl implements IPasswordResetService,IPasswordTokenMail{

    private final IUserService userService;
    private final IForgotPasswordEmailService forgotPasswordEmailService;
    @Autowired
    public ForgotPasswordServiceImpl(
        IEmployeeRepository employeeRepository,
        IStudentRepository studentRepository,
        BCryptPasswordEncoder bCryptPasswordEncoder,
        IUserService userService,
        IForgotPasswordEmailService forgotPasswordEmailService
    ) {
        super(employeeRepository, studentRepository, bCryptPasswordEncoder);
        this.userService = userService;
        this.forgotPasswordEmailService = forgotPasswordEmailService;

    }

    @Override
    public MessageResultResponseDTO resetPassword(PasswordResetRequestDTO requestDTO) {
        Optional<EmployeeModel> employeeOptional = employeeRepository.findByResetToken(requestDTO.getToken());
        if(employeeOptional.isPresent()){
            return handlePasswordReset(employeeOptional.get(),requestDTO, UserType.EMPLOYEE);
        }
        Optional<StudentModel> studentOptional = studentRepository.findByResetToken(requestDTO.getToken());
        if(studentOptional.isPresent()){
            return handlePasswordReset(studentOptional.get(),requestDTO,UserType.STUDENT);
        }
        throw new InvalidTokenException("Invalid or expired reset  token");
    }

    private MessageResultResponseDTO handlePasswordReset(UserModel userModel, PasswordResetRequestDTO resetDTO, UserType type) {
        ForgotPasswordRequestDTO resetPasswordRequestDTO = (ForgotPasswordRequestDTO) resetDTO;
        validateToken(userModel.getTokenExpiry());
        if(!resetPasswordRequestDTO.getNewPassword().equals(resetPasswordRequestDTO.getConfirmPassword())){
            throw new PasswordMismatchException("The new password does not match the confirm password");
        }
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

    @Override
    public MessageResultResponseDTO generateTokenForAnEmailAndSendEmail(String email) {
        boolean verifyEmail = userService.verifyEmail(email);
        if(!verifyEmail){
            throw new InvalidEmailException("Email not found or format is invalid");
        }
        String generateToken = generateResetToken();
        LocalDateTime tokenExpiry = LocalDateTime.now().plusHours(24);

        UserModel userModel = userService.findByMail(email).get();
        userModel.setResetToken(generateToken);
        userModel.setTokenExpiry(tokenExpiry);

        boolean isSaved = userService.saveUser(userModel);
        if(isSaved) {
            forgotPasswordEmailService.sendForgotPasswordEmail(email, generateToken);
            return new MessageResultResponseDTO("Token Generated and Email Send", isSaved);
        }else{
            throw  new InvalidEmailException("Invalid Request");
        }
    }
}
