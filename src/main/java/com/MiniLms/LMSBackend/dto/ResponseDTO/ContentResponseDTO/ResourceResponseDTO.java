package com.MiniLms.LMSBackend.dto.ResponseDTO.ContentResponseDTO;


import com.MiniLms.LMSBackend.model.ContentModels.Resource;
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
    private String exerciseUrl;
    private String solutionUrl;
    private String video;
    private String classPPTUrl;
    private String test;
}
