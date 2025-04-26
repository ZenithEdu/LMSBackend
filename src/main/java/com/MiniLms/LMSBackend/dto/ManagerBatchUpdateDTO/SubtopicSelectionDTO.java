package com.MiniLms.LMSBackend.dto.ManagerBatchUpdateDTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubtopicSelectionDTO {
    @NotBlank(message = "Subtopic ID cannot be blank")
    private String subtopicId;

    @NotBlank(message = "Subtopic name cannot be blank")
    private String subtopicName;

    private LocalDate selectedDate;

    private boolean selected;
}
