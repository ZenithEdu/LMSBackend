package com.MiniLms.LMSBackend.service.BatchService;

public enum BatchProcessStatusCodes {
    PROCESSING,
    COMPLETED,
    FAILED,
    VALIDATING_MANAGER,
    VALIDATING_SUBJECTS,
    CREATING_BATCH,
    PROCESSING_STUDENTS,
    FINALIZING_BATCH
}
