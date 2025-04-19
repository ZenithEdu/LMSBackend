package com.MiniLms.LMSBackend.dto.RequestDTO.ContentRequestDTOs;

import com.MiniLms.LMSBackend.dto.ResponseDTO.ContentResponseDTO.CommonContentResponseDTO;
import com.MiniLms.LMSBackend.model.ContentModels.Resource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.web.multipart.MultipartFile;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonTopicRequestDTO extends CommonContentRequestDTO{
    private ResourceRequestDTO resourceRequestDTO;
}


