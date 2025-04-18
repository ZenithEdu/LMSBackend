package com.MiniLms.LMSBackend.dto.RequestDTO.RegistrationAndLoginRequestDTOS;

import com.MiniLms.LMSBackend.model.UserModelAndSubModels.Gender;
import com.MiniLms.LMSBackend.model.UserModelAndSubModels.Role;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
public class UserRegistrationRequestDTO {
    @Id
    private String id;

    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    private String email;

    @NotNull(message = "Role is mandatory")
    private Role role;

    @NotNull(message = "Gender is mandatory")
    private Gender gender;
}
