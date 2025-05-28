package com.MiniLms.LMSBackend.dto.RequestDTO.RegistrationAndLoginRequestDTOS;

import com.MiniLms.LMSBackend.model.UserModelAndSubModels.EmployeeModel;
import com.MiniLms.LMSBackend.model.UserModelAndSubModels.Gender;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeName("EMPLOYEE")
public class EmployeeRegistrationRequestDTO extends UserRegistrationRequestDTO {

    @NotBlank(message = "Phone number is required")
    @Pattern(
        regexp = "^[6-9]\\d{9}$",
        message = "Phone number must be 10 digits and start with 6, 7, 8, or 9"
    )
    private String phone;

    @NotNull(message = "Gender is mandatory")
    private Gender gender;

    List<String> batchId;

    // And a method to convert from DTO to Entity
    public EmployeeModel toEntity() {
        return EmployeeModel.builder()
            .name(this.getName())
            .email(this.getEmail())
            .phone(this.getPhone())
            .role(this.getRole())
            .gender(this.getGender())
            .batchId(this.batchId)
            .build();
    }
}