package com.MiniLms.LMSBackend.service.serviceImpl.registrationImpl;

import com.MiniLms.LMSBackend.dto.RequestDTO.EmployeeRegistrationRequestDTO;
import com.MiniLms.LMSBackend.dto.ResponseDTO.EmployeeRegistrationResponseDTO;
import com.MiniLms.LMSBackend.dto.ResponseDTO.UserRegistrationResponseDTO;
import com.MiniLms.LMSBackend.dto.RequestDTO.UserRegistrationRequestDTO;
import com.MiniLms.LMSBackend.exceptions.UserAlreadyExistsException;
import com.MiniLms.LMSBackend.model.EmployeeModel;
import com.MiniLms.LMSBackend.repository.IEmployeeRepository;
import com.MiniLms.LMSBackend.service.IRegistrationService;
import com.MiniLms.LMSBackend.service.emailService.IResetPasswordFirstTimeEmailService;
import com.MiniLms.LMSBackend.utils.PasswordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service("employeeRegistrationService")
public class EmployeeRegistrationServiceImpl implements IRegistrationService {

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
    public UserRegistrationResponseDTO register(UserRegistrationRequestDTO userRegistrationRequestDTO) throws UserAlreadyExistsException {
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
