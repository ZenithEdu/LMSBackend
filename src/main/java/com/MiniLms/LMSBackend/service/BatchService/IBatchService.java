package com.MiniLms.LMSBackend.service.BatchService;

import com.MiniLms.LMSBackend.dto.RequestDTO.BatchRequestDTOs.BatchCreationRequestDTO;
import com.MiniLms.LMSBackend.dto.ResponseDTO.BatchResponseDTOs.BatchCreationResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IBatchService {
    BatchCreationResponseDTO createBatchAsync(BatchCreationRequestDTO request, String processId) throws IOException;
}
