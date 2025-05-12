package com.MiniLms.LMSBackend.service.ContentService;

import com.MiniLms.LMSBackend.dto.RequestDTO.ContentRequestDTOs.SubjectRequestDTO;
import com.MiniLms.LMSBackend.dto.ResponseDTO.ContentResponseDTO.SubjectResponseDTO;
import jakarta.validation.Valid;

import java.util.List;

public interface ISubjectService {

    SubjectResponseDTO createSubject(SubjectRequestDTO subjectRequestDTO);
    List<SubjectResponseDTO> getAllSubjects();
    SubjectResponseDTO getSubjectById(String id);
    SubjectResponseDTO updateSubject(String id, SubjectRequestDTO dto);
    void deleteSubject(String id);
    Long subjectCount();
}
