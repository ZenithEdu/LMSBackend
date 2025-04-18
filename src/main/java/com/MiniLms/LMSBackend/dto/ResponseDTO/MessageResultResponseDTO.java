package com.MiniLms.LMSBackend.dto.ResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageResultResponseDTO {
    private String message;
    private boolean success;
}
