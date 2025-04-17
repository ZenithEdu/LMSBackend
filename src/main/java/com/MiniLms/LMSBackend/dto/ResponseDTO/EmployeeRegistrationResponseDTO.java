package com.MiniLms.LMSBackend.dto.ResponseDTO;

import com.MiniLms.LMSBackend.model.EmployeeModel;
import com.MiniLms.LMSBackend.model.UserType;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@JsonTypeName("EMPLOYEE")
public class EmployeeRegistrationResponseDTO extends UserRegistrationResponseDTO {
    private List<String> batchId;

    public static EmployeeRegistrationResponseDTO fromEntity(EmployeeModel employee) {
        return EmployeeRegistrationResponseDTO.builder()
            .id(employee.getId())
            .name(employee.getName())
            .email(employee.getEmail())
            .role(employee.getRole())
            .gender(employee.getGender())
            .batchId(employee.getBatchId())
            .type(UserType.EMPLOYEE)
            .build();
    }

}
