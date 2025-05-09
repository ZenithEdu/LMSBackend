package com.MiniLms.LMSBackend.dto.RequestDTO.RegistrationAndLoginRequestDTOS;

import com.MiniLms.LMSBackend.model.UserModelAndSubModels.EmployeeModel;
import com.fasterxml.jackson.annotation.JsonTypeName;
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