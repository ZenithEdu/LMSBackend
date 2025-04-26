package com.MiniLms.LMSBackend.controller.Batch;


import com.MiniLms.LMSBackend.dto.ManagerBatchUpdateDTO.BatchSelectionsUpdateRequest;
import com.MiniLms.LMSBackend.dto.ManagerBatchUpdateDTO.SubjectSelectionDTO;
import com.MiniLms.LMSBackend.service.BatchService.IBatchService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/batches/{batchId}/subjects/{subjectId}/selections")
public class BatchSelectionManagementController {

    private final IBatchService batchService;

    public BatchSelectionManagementController(
        IBatchService batchService
    ){
        this.batchService = batchService;
    }


    @GetMapping()
    public ResponseEntity<SubjectSelectionDTO> getSubjectSelections(
        @PathVariable String batchId,
        @PathVariable String subjectId) {

        return ResponseEntity.ok(batchService.getSubjectSelections(batchId, subjectId));
    }

    @PatchMapping()
    public ResponseEntity<Void> updateSelections(
        @PathVariable String batchId,
        @PathVariable String subjectId,
        @Valid @RequestBody BatchSelectionsUpdateRequest request) {

        batchService.updateSelections(batchId, subjectId, request);
        return ResponseEntity.noContent().build();
    }


}
