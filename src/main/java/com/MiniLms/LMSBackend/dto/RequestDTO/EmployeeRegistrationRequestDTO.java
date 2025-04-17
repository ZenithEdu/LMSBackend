package com.MiniLms.LMSBackend.dto.RequestDTO;

import com.MiniLms.LMSBackend.model.EmployeeModel;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.NotBlank;
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

    List<String> batchId;

    // And a method to convert from DTO to Entity
    public EmployeeModel toEntity() {
        return EmployeeModel.builder()
            .id(this.getId())
            .name(this.getName())
            .email(this.getEmail())
            .role(this.getRole())
            .gender(this.getGender())
            .batchId(this.batchId)
            .build();
    }
}