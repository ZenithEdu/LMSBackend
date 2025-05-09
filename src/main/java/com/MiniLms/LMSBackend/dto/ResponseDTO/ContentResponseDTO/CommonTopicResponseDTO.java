package com.MiniLms.LMSBackend.dto.ResponseDTO.ContentResponseDTO;

import com.MiniLms.LMSBackend.model.ContentModels.Resource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CommonTopicResponseDTO extends CommonContentResponseDTO{
    private ResourceResponseDTO resourceResponseDTO;
}
