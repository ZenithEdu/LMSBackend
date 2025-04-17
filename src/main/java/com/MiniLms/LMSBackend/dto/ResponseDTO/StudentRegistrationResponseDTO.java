package com.MiniLms.LMSBackend.dto.ResponseDTO;

import com.MiniLms.LMSBackend.model.StudentModel;
import com.MiniLms.LMSBackend.model.UserType;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@JsonTypeName("STUDENT")
public class StudentRegistrationResponseDTO extends UserRegistrationResponseDTO {
    private String batchId;

    public static StudentRegistrationResponseDTO fromEntity(StudentModel student) {
        return StudentRegistrationResponseDTO.builder()
            .id(student.getId())
            .name(student.getName())
            .email(student.getEmail())
            .role(student.getRole())
            .gender(student.getGender())
            .batchId(student.getBatchId())
            .type(UserType.STUDENT)
            .build();
    }

}
