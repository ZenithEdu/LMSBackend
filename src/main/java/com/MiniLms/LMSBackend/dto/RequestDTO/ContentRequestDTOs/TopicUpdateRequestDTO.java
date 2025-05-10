package com.MiniLms.LMSBackend.dto.RequestDTO.ContentRequestDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicUpdateRequestDTO {
    private String name;
    private ResourceRequestDTO resourceRequestDTO;
}
