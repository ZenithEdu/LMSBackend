package com.MiniLms.LMSBackend.dto.ManagerBatchUpdateDTO;


import com.MiniLms.LMSBackend.dto.ResponseDTO.ContentResponseDTO.TopicResponseDTO;
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
    private TopicResponseDTO topicResponseDTO;

    private LocalDate selectedDate;

    private boolean selected;
}
