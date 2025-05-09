package com.MiniLms.LMSBackend.dto.ManagerBatchUpdateDTO;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchSelectionsUpdateRequest {
    @NotEmpty(message = "Topic updates cannot be empty")
    private Set<@Valid TopicUpdateDTO> topicUpdates;
}
