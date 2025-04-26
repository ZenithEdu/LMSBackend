package com.MiniLms.LMSBackend.dto.ManagerBatchUpdateDTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubtopicUpdateDTO {
    @NotBlank(message = "Subtopic ID cannot be blank")
    private String subtopicId;

    private boolean selected;
}
