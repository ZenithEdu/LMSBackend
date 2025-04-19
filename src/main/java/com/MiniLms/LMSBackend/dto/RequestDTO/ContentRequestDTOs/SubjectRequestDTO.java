package com.MiniLms.LMSBackend.dto.RequestDTO.ContentRequestDTOs;

import com.MiniLms.LMSBackend.model.ContentModels.SubjectModel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.HashSet;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectRequestDTO extends CommonContentRequestDTO{
    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 500, message = "Description must be between 10 and 500 characters")
    private String description;

    public SubjectModel toEntity() {
        return SubjectModel.builder()
            .name(this.getName())
            .description(this.description)
            .topicsIds(new HashSet<>()) // Empty initially; topics can be added later
            .build();
    }
}
