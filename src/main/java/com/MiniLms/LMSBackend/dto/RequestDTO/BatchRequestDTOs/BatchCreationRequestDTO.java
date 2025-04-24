package com.MiniLms.LMSBackend.dto.RequestDTO.BatchRequestDTOs;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchCreationRequestDTO {

    @NotBlank(message = "Batch name cannot be blank")
    private String name;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotBlank(message = "Manager ID is required")
    private String managerId;


    @NotEmpty(message = "At least one subject must be selected")
    private Set<String> subjectIds;

    private MultipartFile studentFile;
}
