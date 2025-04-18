package com.MiniLms.LMSBackend.service.UserService;


import com.MiniLms.LMSBackend.dto.ResponseDTO.MessageResultResponseDTO;
import com.MiniLms.LMSBackend.model.UserModel;

import java.util.Optional;

public interface IUserService {
    public boolean verifyEmail(String email);
    public boolean saveUser(UserModel userModel);
    public Optional<UserModel> findByMail(String email);
}
