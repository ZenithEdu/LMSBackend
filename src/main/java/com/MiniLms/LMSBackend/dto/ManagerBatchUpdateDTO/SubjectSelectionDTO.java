package com.MiniLms.LMSBackend.dto.ManagerBatchUpdateDTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectSelectionDTO {
    @NotBlank(message = "Subject ID cannot be blank")
    private String subjectId;

    @NotBlank(message = "Subject name cannot be blank")
    private String subjectName;

    @NotEmpty(message = "At least one topic must be selected")
    private Set<@Valid TopicSelectionDTO> topics;
}

