package com.MiniLms.LMSBackend.service.BatchService;

import com.MiniLms.LMSBackend.dto.ManagerBatchUpdateDTO.BatchSelectionsUpdateRequest;
import com.MiniLms.LMSBackend.dto.ManagerBatchUpdateDTO.SubjectSelectionDTO;
import com.MiniLms.LMSBackend.dto.RequestDTO.BatchRequestDTOs.BatchCreationRequestDTO;
import com.MiniLms.LMSBackend.dto.RequestDTO.RegistrationAndLoginRequestDTOS.StudentRegistrationRequestDTO;
import com.MiniLms.LMSBackend.dto.ResponseDTO.BatchResponseDTOs.BatchCreationResponseDTO;
import com.MiniLms.LMSBackend.dto.ResponseDTO.RegistrationAndLoginResponseDTOS.StudentRegistrationResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IBatchService {
    BatchCreationResponseDTO createBatchAsync(BatchCreationRequestDTO request, String processId) throws IOException;

    BatchCreationResponseDTO getBatchById(String batchId);

    List<BatchCreationResponseDTO> getAllBatchesForManager(String managerId);

    StudentRegistrationResponseDTO saveStudentToBatch(StudentRegistrationRequestDTO studentId, String batchId);

    SubjectSelectionDTO getSubjectSelections(String batchId, String subjectId);
    void updateSelections(String batchId, String subjectId, BatchSelectionsUpdateRequest request);

    List<SubjectSelectionDTO> getBatchCurriculum(String batchId);
}
