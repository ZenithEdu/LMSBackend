package com.MiniLms.LMSBackend.service.BatchService;

public interface IBatchCleanupService {
    void cleanupExpiredBatchesManual();
    void cleanupExpiredBatchesScheduled();
}
