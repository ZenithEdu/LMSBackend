package com.MiniLms.LMSBackend.service.BatchService;


import com.MiniLms.LMSBackend.model.BatchModels.BatchModel;
import com.MiniLms.LMSBackend.repository.BatchRepository.IBatchRepository;
import com.MiniLms.LMSBackend.service.UserService.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;


@Service
public class BatchCleanupServiceImpl implements IBatchCleanupService{


    private static final Logger logger = LoggerFactory.getLogger(BatchCleanupServiceImpl.class);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter FILE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final IBatchRepository batchRepository;
    private final IUserService userService;

    @Value("${logging.batch.cleanup.path:logs/batch-cleanup}")
    private String logDirectoryPath;

    @Value("${logging.batch.cleanup.retention.days:30}")
    private int logRetentionDays;

    @Autowired
    public BatchCleanupServiceImpl(
        IBatchRepository batchRepository,
        IUserService userService
    ){
        this.batchRepository = batchRepository;
        this.userService = userService;
    }


    @Scheduled(cron = "0 0 1 * * ?") // runs daily at 1 AM
    @Transactional
    @Override
    public void cleanupExpiredBatches() {
        LocalDate today = LocalDate.now();
        List<BatchModel> expiredBatches = batchRepository.findByEndDateBefore(today);
        if(expiredBatches == null || expiredBatches.isEmpty()){
            logCleanupResult(today, 0, null);
            logger.info("No batches were deleted on {}", today.format(DATE_FORMAT));
            return;
        }
        int deletedCount = expiredBatches.size();
        StringBuilder batchDetails = new StringBuilder();

        for (BatchModel batch : expiredBatches) {
            int studentCount = batch.getStudentId().size();
            batchDetails.append(String.format(
                "Batch ID: %s, Name: %s, Student Count: %d%n",
                batch.getId(),
                batch.getName(),
                studentCount
            ));

            batch.getStudentId().forEach(userService::deleteUser);
            batchRepository.delete(batch);
        }

        logCleanupResult(today, deletedCount, batchDetails.toString());
        logger.info("Deleted {} batches on {}", deletedCount, today.format(DATE_FORMAT));
    }

    @Scheduled(cron = "0 0 2 * * ?") // Runs daily at 2 AM
    public void cleanupOldLogs() {
        try {
            Path logDir = Paths.get(logDirectoryPath);
            if (!Files.exists(logDir)) {
                return;
            }

            LocalDate cutoffDate = LocalDate.now().minusDays(logRetentionDays);

            try (Stream<Path> logFiles = Files.list(logDir)) {
                logFiles.forEach(path -> {
                    try {
                        String filename = path.getFileName().toString();
                        if (filename.startsWith("batch_cleanup_") && filename.endsWith(".log")) {
                            String dateStr = filename.substring(14, 22);
                            LocalDate fileDate = LocalDate.parse(dateStr, FILE_DATE_FORMAT);

                            if (fileDate.isBefore(cutoffDate)) {
                                Files.delete(path);
                                logger.info("Deleted old log file: {}", path);
                            }
                        }
                    } catch (Exception e) {
                        logger.error("Error processing log file {}: {}", path, e.getMessage());
                    }
                });
            }
        } catch (Exception e) {
            logger.error("Error during log cleanup: {}", e.getMessage());
        }
    }

    private void logCleanupResult(LocalDate date, int deletedCount, String batchDetails) {
        try {
            Path logDir = Paths.get(logDirectoryPath);
            if (!Files.exists(logDir)) {
                Files.createDirectories(logDir);
            }

            String filename = String.format("batch_cleanup_%s.log", date.format(FILE_DATE_FORMAT));
            Path logFile = logDir.resolve(filename);

            String logEntry = String.format(
                "=== Batch Cleanup Report for %s ===%n" +
                    "Batches Deleted: %d%n%s%n",
                date.format(DATE_FORMAT),
                deletedCount,
                batchDetails != null ? batchDetails : "No batches were deleted."
            );

            Files.write(
                logFile,
                logEntry.getBytes(),
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            logger.error("Failed to write cleanup log: {}", e.getMessage());
        }
    }


}
