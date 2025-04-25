package com.MiniLms.LMSBackend.service.BatchService;

import com.MiniLms.LMSBackend.dto.RequestDTO.BatchRequestDTOs.BatchCreationRequestDTO;
import com.MiniLms.LMSBackend.dto.RequestDTO.RegistrationAndLoginRequestDTOS.StudentRegistrationRequestDTO;
import com.MiniLms.LMSBackend.dto.ResponseDTO.BatchResponseDTOs.BatchCreationResponseDTO;
import com.MiniLms.LMSBackend.dto.ResponseDTO.ContentResponseDTO.SubjectResponseDTO;
import com.MiniLms.LMSBackend.dto.ResponseDTO.RegistrationAndLoginResponseDTOS.StudentRegistrationResponseDTO;
import com.MiniLms.LMSBackend.dto.ResponseDTO.RegistrationAndLoginResponseDTOS.UserRegistrationResponseDTO;
import com.MiniLms.LMSBackend.exceptions.ResourceNotFoundException;
import com.MiniLms.LMSBackend.model.BatchModels.BatchModel;
import com.MiniLms.LMSBackend.model.UserModelAndSubModels.UserModel;
import com.MiniLms.LMSBackend.model.UserModelAndSubModels.UserType;
import com.MiniLms.LMSBackend.repository.BatchRepository.IBatchRepository;
import com.MiniLms.LMSBackend.service.ContentService.ISubjectService;
import com.MiniLms.LMSBackend.service.RegistrationService.RegistrationServiceFactory;
import com.MiniLms.LMSBackend.service.RegistrationService.StudentRegistrationServiceImpl;
import com.MiniLms.LMSBackend.service.UserService.IUserService;
import com.MiniLms.LMSBackend.utils.ExcelFileParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BatchServiceImpl implements IBatchService{

    private final IUserService userService;
    private final ISubjectService subjectService;
    private final IBatchRepository batchRepository;
    private final  RegistrationServiceFactory registrationServiceFactory;
    private final BatchProcessService batchProcessService;
    private final Logger log = LoggerFactory.getLogger(BatchServiceImpl.class);

    @Autowired
    public BatchServiceImpl(
        IUserService userService,
        ISubjectService subjectService,
        IBatchRepository batchRepository,
        RegistrationServiceFactory registrationServiceFactory,
        BatchProcessService batchProcessService
    ){
        this.userService = userService;
        this.subjectService = subjectService;
        this.batchRepository = batchRepository;
        this.registrationServiceFactory = registrationServiceFactory;
        this.batchProcessService = batchProcessService;
    }


    @Override
    public BatchCreationResponseDTO createBatchAsync(BatchCreationRequestDTO request,String processId) throws IOException {
        try{
            batchProcessService.updateStatus(processId,BatchProcessStatusCodes.VALIDATING_MANAGER,5);
            UserModel manager = validateManager(request.getManagerId());

            batchProcessService.updateStatus(processId,BatchProcessStatusCodes.VALIDATING_SUBJECTS,10);
            Set<BatchModel.BatchSubject>  batchSubjects = validateAndPrepareSubjects(request.getSubjectIds());

            batchProcessService.updateStatus(processId,BatchProcessStatusCodes.CREATING_BATCH,20);
            BatchModel batchModel = createBatchModel(request,manager,batchSubjects);
            batchModel = batchRepository.save(batchModel);


            batchProcessService.updateStatus(processId,BatchProcessStatusCodes.PROCESSING_STUDENTS,30);
            Set<String> studentIds = parseAndRegisterStudents(request.getStudentFile(),batchModel.getId(),processId);


            batchProcessService.updateStatus(processId,BatchProcessStatusCodes.FINALIZING_BATCH,90);
            batchModel.setStudentId(studentIds);
            batchModel = batchRepository.save(batchModel);

            return mapToResponseDTO(batchModel);
        }catch (Exception e){
            batchProcessService.updateStatus(processId,BatchProcessStatusCodes.FAILED,0,e.getMessage(),true);
            throw e;
        }
    }

    private Set<String> parseAndRegisterStudents(MultipartFile studentFile, String id, String processId) throws IOException {
        if (studentFile == null || studentFile.isEmpty()) {
            throw new IllegalArgumentException("Student Excel file is required");
        }

        long actualSize = studentFile.getBytes().length;
        log.info("Processing file with actual size: {}", actualSize);

        List<StudentRegistrationRequestDTO> studentRegistrationRequestDTOS = ExcelFileParser.parseExcelFile(studentFile);

        StudentRegistrationServiceImpl registrationService = (StudentRegistrationServiceImpl) registrationServiceFactory.getService(UserType.STUDENT);

        int chunkSize = 20;
        int totalStudents = studentRegistrationRequestDTOS.size();

        Set<String> studentIds = new HashSet<>();

        for(int i = 0; i < totalStudents; i+= chunkSize){
            List<StudentRegistrationRequestDTO> chunk = studentRegistrationRequestDTOS.subList(i,Math.min(i+chunkSize,totalStudents));
            List<StudentRegistrationResponseDTO> responseDTOS = registrationService.registerMultipleStudents(chunk,id);

            responseDTOS.stream()
                .map(UserRegistrationResponseDTO::getId)
                .forEach(studentIds::add);

            int progress = 30 + (int)(60 * ((double)i/totalStudents));
            batchProcessService.updateStatus(processId,BatchProcessStatusCodes.PROCESSING_STUDENTS,progress);
        }
        return studentIds;
    }

    private BatchModel createBatchModel(BatchCreationRequestDTO request, UserModel manager, Set<BatchModel.BatchSubject> batchSubjects) {
        return BatchModel.builder()
            .name(request.getName())
            .startDate(request.getStartDate())
            .endDate(request.getEndDate())
            .managerId(manager.getId())
            .subjects(batchSubjects)
            .build();
    }
    private BatchCreationResponseDTO mapToResponseDTO(BatchModel batch) {
        return BatchCreationResponseDTO.builder()
            .id(batch.getId())
            .name(batch.getName())
            .startDate(batch.getStartDate())
            .endDate(batch.getEndDate())
            .managerId(batch.getManagerId())
            .subjects(mapBatchSubjects(batch.getSubjects()))
            .studentId(batch.getStudentId()) // Convert List to Set
            .build();
    }

    private Set<BatchCreationResponseDTO.BatchSubjectResponse> mapBatchSubjects(Set<BatchModel.BatchSubject> batchSubjects) {
        return batchSubjects.stream()
            .map(batchSubject -> BatchCreationResponseDTO.BatchSubjectResponse.builder()
                .subjectId(batchSubject.getSubjectId())
                .selectedTopics(mapSelectedTopics(batchSubject.getSelectedTopics()))
                .build())
            .collect(Collectors.toSet());
    }

    private Set<BatchCreationResponseDTO.SelectedTopicResponse> mapSelectedTopics(Set<BatchModel.SelectedTopic> selectedTopics) {
        return selectedTopics.stream()
            .map(topic -> BatchCreationResponseDTO.SelectedTopicResponse.builder()
                .topicId(topic.getTopicId())
                .selectedDate(topic.getSelectedDate())
                .selectedSubtopics(mapSelectedSubtopics(topic.getSelectedSubtopics()))
                .build())
            .collect(Collectors.toSet());
    }

    private Set<BatchCreationResponseDTO.SelectedSubtopicResponse> mapSelectedSubtopics(Set<BatchModel.SelectedSubtopic> selectedSubtopics) {
        return selectedSubtopics.stream()
            .map(subtopic -> BatchCreationResponseDTO.SelectedSubtopicResponse.builder()
                .subtopicId(subtopic.getSubtopicId())
                .selectedDate(subtopic.getSelectedDate())
                .build())
            .collect(Collectors.toSet());
    }

    private Set<BatchModel.BatchSubject> validateAndPrepareSubjects(Set<String> subjectIds) {
        return subjectIds.stream()
            .map(subjectId -> {
                SubjectResponseDTO subject = subjectService.getSubjectById(subjectId);
                if(subject == null){
                    throw new ResourceNotFoundException("Subject not found with ID : " + subjectId);
                }

                return BatchModel.BatchSubject.builder()
                    .subjectId(subjectId)
                    .selectedTopics(new HashSet<>())
                    .build();

            }).collect(Collectors.toSet());
    }

    private UserModel validateManager(String managerId) {
        UserModel manager = userService.findById(managerId)
            .orElseThrow(() -> new ResourceNotFoundException("Manager not found with ID : " + managerId));
        return manager;
    }
}
