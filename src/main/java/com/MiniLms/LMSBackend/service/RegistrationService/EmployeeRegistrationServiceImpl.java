package com.MiniLms.LMSBackend.service.RegistrationService;

import com.MiniLms.LMSBackend.dto.RequestDTO.RegistrationAndLoginRequestDTOS.EmployeeRegistrationRequestDTO;
import com.MiniLms.LMSBackend.dto.ResponseDTO.RegistrationAndLoginResponseDTOS.EmployeeRegistrationResponseDTO;
import com.MiniLms.LMSBackend.dto.ResponseDTO.RegistrationAndLoginResponseDTOS.UserRegistrationResponseDTO;
import com.MiniLms.LMSBackend.dto.RequestDTO.RegistrationAndLoginRequestDTOS.UserRegistrationRequestDTO;
import com.MiniLms.LMSBackend.exceptions.UserAlreadyExistsException;
import com.MiniLms.LMSBackend.model.UserModelAndSubModels.EmployeeModel;
import com.MiniLms.LMSBackend.repository.UserRepositories.IEmployeeRepository;
import com.MiniLms.LMSBackend.service.emailService.IResetPasswordFirstTimeEmailService;
import com.MiniLms.LMSBackend.utils.PasswordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service("employeeRegistrationService")
public class EmployeeRegistrationServiceImpl implements IRegistrationService, IGenerateResetToken {

    private final IEmployeeRepository employeeRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final IResetPasswordFirstTimeEmailService resetPasswordFirstTimeEmailService;

    @Autowired
    public EmployeeRegistrationServiceImpl(
        IEmployeeRepository employeeRepository,
        BCryptPasswordEncoder bCryptPasswordEncoder,
        IResetPasswordFirstTimeEmailService resetPasswordFirstTimeEmailService
    ){
        this.employeeRepository = employeeRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.resetPasswordFirstTimeEmailService = resetPasswordFirstTimeEmailService;
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN')")
    public UserRegistrationResponseDTO register(UserRegistrationRequestDTO userRegistrationRequestDTO) throws UserAlreadyExistsException {
        return registerEmployee(userRegistrationRequestDTO);
    }

    public UserRegistrationResponseDTO createFirstAdmin(UserRegistrationRequestDTO userRegistrationRequestDTO){
        return registerEmployee(userRegistrationRequestDTO);
    }


    private UserRegistrationResponseDTO registerEmployee(UserRegistrationRequestDTO userRegistrationRequestDTO) throws UserAlreadyExistsException{
        EmployeeRegistrationRequestDTO employeeRegistrationRequestDto = (EmployeeRegistrationRequestDTO) userRegistrationRequestDTO;
        Optional<EmployeeModel> hasEmployee = employeeRepository.findByEmail(userRegistrationRequestDTO.getEmail());

        if(hasEmployee.isPresent()){
            throw new UserAlreadyExistsException("User with email "+ employeeRegistrationRequestDto.getEmail() +" already exists");
        }
        String password = PasswordGenerator.generateTemporaryPassword();
        String resetToken = generateResetToken();
        LocalDateTime tokenExpiry = LocalDateTime.now().plusHours(24);

        EmployeeModel employeeModel = employeeRegistrationRequestDto.toEntity();
        employeeModel.setPassword(bCryptPasswordEncoder.encode(password));
        employeeModel.setResetToken(resetToken);
        employeeModel.setTokenExpiry(tokenExpiry);

        employeeModel = employeeRepository.save(employeeModel);

        resetPasswordFirstTimeEmailService.sendResetEmail(employeeModel.getEmail(),password,resetToken);
        System.out.println("Send Email");

        EmployeeRegistrationResponseDTO response = EmployeeRegistrationResponseDTO.fromEntity(employeeModel);

        return response;
    }
}
