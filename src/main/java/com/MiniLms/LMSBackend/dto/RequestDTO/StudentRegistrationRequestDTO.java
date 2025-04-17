package com.MiniLms.LMSBackend.dto.RequestDTO;

import com.MiniLms.LMSBackend.model.StudentModel;
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

    public StudentModel toEntity() {
        return StudentModel.builder()
            .id(this.getId())
            .name(this.getName())
            .email(this.getEmail())
            .role(this.getRole())
            .gender(this.getGender())
            .batchId(this.getBatchId())
            .build();
    }
}
