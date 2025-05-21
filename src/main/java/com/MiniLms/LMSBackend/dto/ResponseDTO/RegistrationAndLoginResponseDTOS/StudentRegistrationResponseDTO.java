package com.MiniLms.LMSBackend.dto.ResponseDTO.RegistrationAndLoginResponseDTOS;

import com.MiniLms.LMSBackend.model.UserModelAndSubModels.StudentModel;
import com.MiniLms.LMSBackend.model.UserModelAndSubModels.UserType;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@JsonTypeName("STUDENT")
public class StudentRegistrationResponseDTO extends UserRegistrationResponseDTO {
    private String branch;
    private String batchId;

    public static StudentRegistrationResponseDTO fromEntity(StudentModel student) {
        return StudentRegistrationResponseDTO.builder()
            .id(student.getId())
            .name(student.getName())
            .email(student.getEmail())
            .phone(student.getPhone())
            .role(student.getRole())
            .gender(student.getGender())
            .batchId(student.getBatchId())
            .branch(student.getBranch())
            .type(UserType.STUDENT)
            .build();
    }
}
