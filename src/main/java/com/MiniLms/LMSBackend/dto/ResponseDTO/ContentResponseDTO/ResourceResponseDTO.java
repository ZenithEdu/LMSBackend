package com.MiniLms.LMSBackend.dto.ResponseDTO.ContentResponseDTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceResponseDTO {
    private String additionalResourcesUrl;
    private String exerciseUrl;
    private String solutionUrl;
    private String practiceUrl;
    private String article;
    private String video;
}
