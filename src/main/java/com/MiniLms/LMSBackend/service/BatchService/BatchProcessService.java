package com.MiniLms.LMSBackend.service.BatchService;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BatchProcessService {
    private final Map<String,BatchProcessStatus> statusMap = new ConcurrentHashMap<>();

    public String initializeProcess(){
        String processId = UUID.randomUUID().toString();
        statusMap.put(processId,new BatchProcessStatus(processId));
        return processId;
    }

    private void updateStatusMain(String processId, BatchProcessStatusCodes status, int progress,String batchId, String errorMessage){
        BatchProcessStatus batchStatus = statusMap.get(processId);
        if(batchStatus != null){
            batchStatus.setStatus(status);
            batchStatus.setProgress(progress);
            batchStatus.setLastUpdated(new Date());
            if(StringUtils.hasText(batchId)){
                batchStatus.setBatchId(batchId);
            }
            if(StringUtils.hasText(errorMessage)){
                batchStatus.setErrorMessage(errorMessage);
            }
        }
    }

    public void updateStatus(String processId, BatchProcessStatusCodes statusCodes, int progress){
        updateStatusMain(processId,statusCodes,progress,null,null);
    }
    public void updateStatus(String processId, BatchProcessStatusCodes statusCodes, int progress, String batchIdOrError, boolean hasError){
        if(hasError){
            updateStatusMain(processId,statusCodes,progress,null,batchIdOrError);
        }else{
            updateStatusMain(processId,statusCodes,progress,batchIdOrError,null);
        }
    }

    public BatchProcessStatus getStatus(String processId) {
        return statusMap.get(processId);
    }

    @Getter @Setter
    public static class BatchProcessStatus{
        private final String processId;
        private BatchProcessStatusCodes status = BatchProcessStatusCodes.PROCESSING;
        private int progress = 0;
        private Date lastUpdated = new Date();
        private String batchId;
        private String errorMessage;

        public BatchProcessStatus(String processId){
            this.processId = processId;
        }
    }
}
