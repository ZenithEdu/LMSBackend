package com.MiniLms.LMSBackend.dto.ManagerBatchUpdateDTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicUpdateDTO {
    @NotBlank(message = "Topic ID cannot be blank")
    private String topicId;

    private boolean selected;
}
