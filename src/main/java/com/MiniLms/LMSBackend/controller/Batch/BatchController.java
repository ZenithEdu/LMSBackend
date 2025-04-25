package com.MiniLms.LMSBackend.controller.Batch;

import com.MiniLms.LMSBackend.dto.RequestDTO.BatchRequestDTOs.BatchCreationRequestDTO;
import com.MiniLms.LMSBackend.dto.ResponseDTO.BatchResponseDTOs.BatchCreationResponseDTO;
import com.MiniLms.LMSBackend.service.BatchService.BatchProcessService;
import com.MiniLms.LMSBackend.service.BatchService.BatchProcessStatusCodes;
import com.MiniLms.LMSBackend.service.BatchService.IBatchService;
import com.MiniLms.LMSBackend.utils.InMemoryMultipartFile;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/api/batches")
public class BatchController {
    private final IBatchService batchService;
    private final BatchProcessService batchProcessService;
    private final Logger logger = LoggerFactory.getLogger(BatchController.class);

    @Autowired
    public BatchController(
        IBatchService batchService,
        BatchProcessService batchProcessService
    ){
        this.batchService = batchService;
        this.batchProcessService = batchProcessService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> createBatch(
        @Valid @RequestPart("batchCreationRequestDTO") BatchCreationRequestDTO batchCreationRequestDTO,
        @RequestPart("studentFile")MultipartFile file
    ){
        log.info("Received file - name: {}, size: {}, content type: {}",
            file.getOriginalFilename(),
            file.getSize(),
            file.getContentType());

        byte[] fileBytes;
        try{
            fileBytes = file.getBytes();
        }catch (IOException e){
            throw new RuntimeException("Failed to read file content",e);
        }

        String processId = batchProcessService.initializeProcess();

        CompletableFuture.runAsync(() -> {
            try{

                MultipartFile fileCopy = new InMemoryMultipartFile(
                    "studentFile",
                    file.getOriginalFilename(),
                    file.getContentType(),
                    fileBytes
                );

                batchCreationRequestDTO.setStudentFile(fileCopy);
                BatchCreationResponseDTO response = batchService.createBatchAsync(
                    batchCreationRequestDTO,
                    processId
                );
                batchProcessService.updateStatus(
                    processId,
                    BatchProcessStatusCodes.COMPLETED,
                    100,
                    response.getId(),
                    false
                );
            }catch (Exception e){
                batchProcessService.updateStatus(
                    processId,
                    BatchProcessStatusCodes.FAILED,
                    0,
                    e.getMessage(),
                    true
                );
            }
        });

        return ResponseEntity.accepted().body(Map.of(
            "message","Batch creation started",
            "processId", processId,
            "statusUrl","/api/batches/status/"+processId
        ));
    }
    @GetMapping("/status/{processId}")
    public ResponseEntity<BatchProcessService.BatchProcessStatus> getStatus(
        @PathVariable String processId
    ) {
        BatchProcessService.BatchProcessStatus status =
            batchProcessService.getStatus(processId);

        if (status == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(status);
    }
}
