package com.MiniLms.LMSBackend.dto.ResponseDTO;

import com.MiniLms.LMSBackend.model.Gender;
import com.MiniLms.LMSBackend.model.Role;
import com.MiniLms.LMSBackend.model.UserType;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class UserRegistrationResponseDTO {
    private String id;
    private String name;
    private String email;
    private Role role;
    private Gender gender;
    private UserType type;
}

