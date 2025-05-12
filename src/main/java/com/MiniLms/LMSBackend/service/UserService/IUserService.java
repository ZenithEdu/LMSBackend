package com.MiniLms.LMSBackend.service.UserService;


import com.MiniLms.LMSBackend.dto.ResponseDTO.RegistrationAndLoginResponseDTOS.UserRegistrationResponseDTO;
import com.MiniLms.LMSBackend.model.UserModelAndSubModels.UserModel;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;

public interface IUserService {
    public boolean verifyEmail(String email);
    public boolean saveUser(UserModel userModel);
    public Optional<UserModel> findByMail(String email);
    public Optional<UserModel> findById(String id);
    void deleteUser(String userId);
    Long studentCount();
    Long employeeCount();
    void deleteEmployee(String id);
    List<UserRegistrationResponseDTO> findAllByRole(String filterRole);
}
