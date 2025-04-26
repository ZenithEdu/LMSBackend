package com.MiniLms.LMSBackend.controller.Batch;


import com.MiniLms.LMSBackend.dto.ManagerBatchUpdateDTO.SubjectSelectionDTO;
import com.MiniLms.LMSBackend.service.BatchService.IBatchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/batches/{batchId}/curriculum")
public class StudentViewContoller {

    private final IBatchService batchService;

    public StudentViewContoller(
        IBatchService batchService
    ){
        this.batchService = batchService;
    }

    @GetMapping
    public ResponseEntity<List<SubjectSelectionDTO>> getBatchCurriculum(
        @PathVariable String batchId) {
        List<SubjectSelectionDTO> curriculum = batchService.getBatchCurriculum(batchId);
        return ResponseEntity.ok(curriculum);
    }
}
