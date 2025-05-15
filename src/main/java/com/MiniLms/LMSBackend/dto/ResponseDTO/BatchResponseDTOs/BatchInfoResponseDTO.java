package com.MiniLms.LMSBackend.dto.ResponseDTO.BatchResponseDTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchInfoResponseDTO {

    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private String managerName;
    private Set<String> subjects;
    private Integer numberOfStudents;
}
