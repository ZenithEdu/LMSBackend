package com.MiniLms.LMSBackend.service.ContentService;

import com.MiniLms.LMSBackend.dto.RequestDTO.ContentRequestDTOs.TopicRequestDTO;
import com.MiniLms.LMSBackend.dto.ResponseDTO.ContentResponseDTO.TopicResponseDTO;

import java.io.IOException;
import java.util.List;

public interface ITopicService {
    TopicResponseDTO createTopic(TopicRequestDTO dto, String subjectId) throws IOException;

    TopicResponseDTO getTopic(String id);

    List<TopicResponseDTO> getTopicsBySubject(String subjectId);

    void deleteTopic(String subjectId, String id);
}
