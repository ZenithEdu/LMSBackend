package com.MiniLms.LMSBackend.controller.Content;

import com.MiniLms.LMSBackend.dto.RequestDTO.ContentRequestDTOs.SubjectRequestDTO;
import com.MiniLms.LMSBackend.dto.ResponseDTO.ContentResponseDTO.SubjectResponseDTO;
import com.MiniLms.LMSBackend.service.ContentService.ISubjectService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/content/subject")
public class SubjectController {
    private final ISubjectService subjectService;

    @Autowired
    public SubjectController(
        ISubjectService subjectService
    ){
        this.subjectService = subjectService;
    }

    // Create a subject
    @PostMapping
    public ResponseEntity<SubjectResponseDTO> createSubject(@Valid @RequestBody SubjectRequestDTO subjectRequestDTO) {
        SubjectResponseDTO response = subjectService.createSubject(subjectRequestDTO);
        return ResponseEntity.ok(response);
    }

    // Get all subjects
    @GetMapping
    public ResponseEntity<List<SubjectResponseDTO>> getAllSubjects() {
        List<SubjectResponseDTO> subjects = subjectService.getAllSubjects();
        return ResponseEntity.ok(subjects);
    }

    // Get subject by ID
    @GetMapping("/{id}")
    public ResponseEntity<SubjectResponseDTO> getSubjectById(@PathVariable String id) {
        SubjectResponseDTO subject = subjectService.getSubjectById(id);
        return ResponseEntity.ok(subject);
    }

    // Update subject
    @PutMapping("/{id}")
    public ResponseEntity<SubjectResponseDTO> updateSubject(@PathVariable String id,
                                                            @Valid @RequestBody SubjectRequestDTO dto) {
        SubjectResponseDTO updated = subjectService.updateSubject(id, dto);
        return ResponseEntity.ok(updated);
    }

    // Delete subject
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubject(@PathVariable String id) {
        subjectService.deleteSubject(id);
        return ResponseEntity.noContent().build();
    }

}
