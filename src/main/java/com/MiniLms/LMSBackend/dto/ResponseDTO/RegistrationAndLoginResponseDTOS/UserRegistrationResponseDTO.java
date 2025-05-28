package com.MiniLms.LMSBackend.dto.ResponseDTO.RegistrationAndLoginResponseDTOS;

import com.MiniLms.LMSBackend.model.UserModelAndSubModels.Gender;
import com.MiniLms.LMSBackend.model.UserModelAndSubModels.Role;
import com.MiniLms.LMSBackend.model.UserModelAndSubModels.UserType;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class UserRegistrationResponseDTO {
    private String id;
    private String name;
    private String email;
    private Role role;
    private UserType type;
}

