package com.MiniLms.LMSBackend.dto.RequestDTO.RegistrationAndLoginRequestDTOS;

import com.MiniLms.LMSBackend.model.UserModelAndSubModels.StudentModel;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeName("STUDENT")
public class StudentRegistrationRequestDTO extends UserRegistrationRequestDTO{
    private String uniId;
    private String batchId;
    private String branch;

    public StudentModel toEntity() {
        return StudentModel.builder()
            .name(this.getName())
            .email(this.getEmail())
            .role(this.getRole())
            .uniId(this.getUniId())
            .batchId(this.getBatchId())
            .branch(this.getBranch())
            .build();
    }
}
