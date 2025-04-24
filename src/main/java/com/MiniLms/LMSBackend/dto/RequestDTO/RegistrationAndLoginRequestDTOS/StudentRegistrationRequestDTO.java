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
    private String batchId;
    private String branch;

    public StudentModel toEntity() {
        return StudentModel.builder()
            .id(this.getId())
            .name(this.getName())
            .email(this.getEmail())
            .phone(this.getPhone())
            .role(this.getRole())
            .gender(this.getGender())
            .batchId(this.getBatchId())
            .branch(this.branch)
            .build();
    }
}
