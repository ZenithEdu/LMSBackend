package com.MiniLms.LMSBackend.service.ContentService;

import com.MiniLms.LMSBackend.dto.RequestDTO.ContentRequestDTOs.SubtopicRequestDTO;
import com.MiniLms.LMSBackend.dto.ResponseDTO.ContentResponseDTO.SubtopicResponseDTO;

import java.io.IOException;
import java.util.List;

public interface ISubtopicService {
    SubtopicResponseDTO createSubtopic(SubtopicRequestDTO subtopicDTO, String subjectId, String topicId) throws IOException;

    SubtopicResponseDTO getSubtopic(String id);

    SubtopicResponseDTO updateSubtopic(String id, SubtopicRequestDTO subtopicDTO);

    List<SubtopicResponseDTO> findAllByTopicId(String subjectId, String topicId);

    void deleteSubtopic(String subjectId, String topicId, String id);

    void deleteAllByTopicId(String topicId);
}
