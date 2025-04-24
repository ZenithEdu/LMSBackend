package com.MiniLms.LMSBackend.controller.Content;

import com.MiniLms.LMSBackend.dto.RequestDTO.ContentRequestDTOs.ResourceRequestDTO;
import com.MiniLms.LMSBackend.dto.RequestDTO.ContentRequestDTOs.SubtopicRequestDTO;
import com.MiniLms.LMSBackend.dto.ResponseDTO.ContentResponseDTO.SubtopicResponseDTO;
import com.MiniLms.LMSBackend.service.ContentService.ISubtopicService;
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
@RequestMapping("/api/content/subject/{subjectId}/topic/{topicId}/subtopic")
public class SubtopicController {

    private final ISubtopicService subtopicService;

    @Autowired
    public SubtopicController(
        ISubtopicService subtopicService
    ) {
        this.subtopicService = subtopicService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SubtopicResponseDTO> createSubtopic(
        @PathVariable String subjectId,
        @PathVariable String topicId,
        @ModelAttribute SubtopicRequestDTO subtopicDTO,
        @ModelAttribute ResourceRequestDTO resourceRequestDTO
    ) throws IOException {
        subtopicDTO.setResourceRequestDTO(resourceRequestDTO);
        SubtopicResponseDTO responseDTO = subtopicService.createSubtopic(subtopicDTO, subjectId, topicId);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubtopicResponseDTO> getSubtopic(@PathVariable String id) {
        SubtopicResponseDTO responseDTO = subtopicService.getSubtopic(id);
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubtopicResponseDTO> updateSubtopic(
        @PathVariable String id,
        @RequestBody SubtopicRequestDTO subtopicDTO
    ) {
        SubtopicResponseDTO responseDTO = subtopicService.updateSubtopic(id, subtopicDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    public ResponseEntity<?> findAllSubtopicsWithTopicId(
        @PathVariable String subjectId,
        @PathVariable String topicId
    ) {
        List<SubtopicResponseDTO> allSubtopics = subtopicService.findAllByTopicId(subjectId, topicId);
        return ResponseEntity.ok(allSubtopics);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubtopic(
        @PathVariable String subjectId,
        @PathVariable String topicId,
        @PathVariable String id
    ) {
        subtopicService.deleteSubtopic(subjectId, topicId, id);
        return ResponseEntity.noContent().build();
    }
}