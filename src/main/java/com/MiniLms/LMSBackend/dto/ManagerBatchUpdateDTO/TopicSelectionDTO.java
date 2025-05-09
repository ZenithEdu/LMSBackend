package com.MiniLms.LMSBackend.dto.ManagerBatchUpdateDTO;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.Set;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopicSelectionDTO {
    @NotBlank(message = "Topic ID cannot be blank")
    private String topicId;

    @NotBlank(message = "Topic name cannot be blank")
    private String topicName;

    private LocalDate selectedDate;

    private boolean selected;
}
