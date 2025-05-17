package com.MiniLms.LMSBackend.controller.Content;

import com.MiniLms.LMSBackend.dto.RequestDTO.ContentRequestDTOs.ResourceRequestDTO;
import com.MiniLms.LMSBackend.dto.RequestDTO.ContentRequestDTOs.TopicRequestDTO;
import com.MiniLms.LMSBackend.dto.RequestDTO.ContentRequestDTOs.TopicUpdateRequestDTO;
import com.MiniLms.LMSBackend.dto.ResponseDTO.ContentResponseDTO.TopicResponseDTO;
import com.MiniLms.LMSBackend.service.ContentService.ITopicService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/content/subject/{subjectId}/topic")
public class TopicController {

    private final ITopicService topicService;

    @Autowired
    public TopicController(
        ITopicService topicService
    ){
        this.topicService = topicService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TopicResponseDTO> createTopic(
        @PathVariable String subjectId,
        @ModelAttribute TopicRequestDTO topicDTO,
        @ModelAttribute ResourceRequestDTO resourceRequestDTO
    ) throws IOException {
        topicDTO.setResourceRequestDTO(resourceRequestDTO);
        TopicResponseDTO responseDTO = topicService.createTopic(topicDTO,subjectId);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TopicResponseDTO> getTopic(@PathVariable String  id) {
        TopicResponseDTO responseDTO = topicService.getTopic(id);
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TopicResponseDTO> updateTopic(@PathVariable String id, @RequestBody TopicUpdateRequestDTO topicDTO) throws IOException {
        TopicResponseDTO responseDTO = topicService.updateTopic(id,topicDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<TopicResponseDTO>> findAllTopicsWithSubjectId(@PathVariable String subjectId){
        List<TopicResponseDTO> allTopics = topicService.findAllBySubjectId(subjectId);
        return ResponseEntity.ok(allTopics);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTopic(@PathVariable String subjectId, @PathVariable String id) {
        topicService.deleteTopic(subjectId,id);
        return ResponseEntity.noContent().build();
    }

}
