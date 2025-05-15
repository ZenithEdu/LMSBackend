package com.MiniLms.LMSBackend.service.BatchService;

import com.MiniLms.LMSBackend.dto.ManagerBatchUpdateDTO.*;
import com.MiniLms.LMSBackend.dto.RequestDTO.BatchRequestDTOs.BatchCreationRequestDTO;
import com.MiniLms.LMSBackend.dto.RequestDTO.RegistrationAndLoginRequestDTOS.StudentRegistrationRequestDTO;
import com.MiniLms.LMSBackend.dto.ResponseDTO.BatchResponseDTOs.BatchCreationResponseDTO;
import com.MiniLms.LMSBackend.dto.ResponseDTO.BatchResponseDTOs.BatchInfoResponseDTO;
import com.MiniLms.LMSBackend.dto.ResponseDTO.ContentResponseDTO.SubjectResponseDTO;
import com.MiniLms.LMSBackend.dto.ResponseDTO.RegistrationAndLoginResponseDTOS.StudentRegistrationResponseDTO;
import com.MiniLms.LMSBackend.dto.ResponseDTO.RegistrationAndLoginResponseDTOS.UserRegistrationResponseDTO;
import com.MiniLms.LMSBackend.exceptions.ResourceNotFoundException;
import com.MiniLms.LMSBackend.model.BatchModels.BatchModel;
import com.MiniLms.LMSBackend.model.ContentModels.SubjectModel;
import com.MiniLms.LMSBackend.model.ContentModels.TopicModel;
import com.MiniLms.LMSBackend.model.UserModelAndSubModels.UserModel;
import com.MiniLms.LMSBackend.model.UserModelAndSubModels.UserType;
import com.MiniLms.LMSBackend.repository.BatchRepository.IBatchRepository;
import com.MiniLms.LMSBackend.repository.ContentRepositories.ISubjectRepository;
import com.MiniLms.LMSBackend.repository.ContentRepositories.ITopicRepository;
import com.MiniLms.LMSBackend.service.ContentService.ISubjectService;
import com.MiniLms.LMSBackend.service.RegistrationService.IRegistrationService;
import com.MiniLms.LMSBackend.service.RegistrationService.RegistrationServiceFactory;
import com.MiniLms.LMSBackend.service.RegistrationService.StudentRegistrationServiceImpl;
import com.MiniLms.LMSBackend.service.UserService.IUserService;
import com.MiniLms.LMSBackend.utils.ExcelFileParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BatchServiceImpl implements IBatchService{

    private final IUserService userService;
    private final ISubjectService subjectService;
    private final IBatchRepository batchRepository;
    private final ISubjectRepository subjectRepository;
    private final ITopicRepository topicRepository;
    private final  RegistrationServiceFactory registrationServiceFactory;
    private final BatchProcessService batchProcessService;
    private final Logger log = LoggerFactory.getLogger(BatchServiceImpl.class);

    @Autowired
    public BatchServiceImpl(
        IUserService userService,
        ISubjectService subjectService,
        IBatchRepository batchRepository,
        ISubjectRepository subjectRepository,
        ITopicRepository topicRepository,
        RegistrationServiceFactory registrationServiceFactory,
        BatchProcessService batchProcessService
    ){
        this.userService = userService;
        this.subjectService = subjectService;
        this.batchRepository = batchRepository;
        this.subjectRepository = subjectRepository;
        this.topicRepository = topicRepository;
        this.registrationServiceFactory = registrationServiceFactory;
        this.batchProcessService = batchProcessService;
    }


    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public BatchCreationResponseDTO createBatchAsync(BatchCreationRequestDTO request,String processId) throws IOException {
        try{

            Optional<BatchModel> existingBatch = batchRepository.findByNameIgnoreCase(request.getName());
            if (existingBatch.isPresent()) {
                String errorMsg = "Batch with this name already exists (case-insensitive) and cannot be created.";
                throw new IllegalArgumentException(errorMsg);
            }


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

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STUDENT')")
    public BatchCreationResponseDTO getBatchById(String batchId) {
        Optional<BatchModel> hasBatch = batchRepository.findById(batchId);
        if(hasBatch.isEmpty()){
            throw new ResourceNotFoundException("No Such batch Exists with Id : " + batchId);
        }
        return mapToResponseDTO(hasBatch.get());
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public List<BatchInfoResponseDTO> getAllBatchesForManager(String managerId) {
        List<BatchModel> allBatches = batchRepository.findByManagerId(managerId);
        return cumulateBatches(allBatches);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<BatchInfoResponseDTO> getAllBatches() {
        List<BatchModel> allBatches = batchRepository.findAll();
        return cumulateBatches(allBatches);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Long getBatchCount() {
        return batchRepository.count();
    }

    @Override
    @PreAuthorize("hasRole('MANAGER')")
    public StudentRegistrationResponseDTO saveStudentToBatch(StudentRegistrationRequestDTO student, String batchId) {
        Optional<BatchModel> hasBatch = batchRepository.findById(batchId);
        if(hasBatch.isEmpty()){
            throw new ResourceNotFoundException("No Batch exists with id : " + batchId);
        }
        IRegistrationService registrationService = registrationServiceFactory.getService(UserType.STUDENT);
        StudentRegistrationResponseDTO responseDTO = (StudentRegistrationResponseDTO) registrationService.register(student);
        BatchModel model = hasBatch.get();
        model.getStudentId().add(responseDTO.getId());
        batchRepository.save(model);
        return responseDTO;
    }

    @Override
    @PreAuthorize("hasRole('MANAGER')")
    public SubjectSelectionDTO getSubjectSelections(String batchId, String subjectId) {
        BatchModel batch = batchRepository.findById(batchId)
            .orElseThrow(() -> new ResourceNotFoundException("Batch not found"));

        SubjectModel subject = subjectRepository.findById(subjectId)
            .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));

        return mapToSelectionDTO(batch, subject);
    }

    @Override
    @PreAuthorize("hasRole('MANAGER')")
    public void updateSelections(String batchId, String subjectId, BatchSelectionsUpdateRequest request) {
        BatchModel batch = batchRepository.findById(batchId)
            .orElseThrow(() -> new ResourceNotFoundException("Batch not found"));

        // Find or create BatchSubject
        BatchModel.BatchSubject batchSubject = findOrCreateBatchSubject(batch, subjectId);

        updateTopicSelections(batchSubject, request.getTopicUpdates());

        batchRepository.save(batch);
    }

    @Override
    @PreAuthorize("hasRole('STUDENT')")
    public List<SubjectSelectionDTO> getBatchCurriculum(String batchId) {
        BatchModel batch = batchRepository.findById(batchId)
            .orElseThrow(() -> new ResourceNotFoundException("Batch not found"));

        List<SubjectSelectionDTO> curriculum = new ArrayList<>();
        for (BatchModel.BatchSubject batchSubject : batch.getSubjects()) {
            SubjectModel subject = subjectRepository.findById(batchSubject.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));

            curriculum.add(mapToStudentViewDTO(batchSubject, subject));
        }
        return curriculum;
    }


    private List<BatchInfoResponseDTO> cumulateBatches(List<BatchModel> batches){
        List<BatchInfoResponseDTO> responseDTOS = new ArrayList<>();
        for(BatchModel batchModel : batches){
            String managerName = "";
           Optional<UserModel> manager  = userService.findById(batchModel.getManagerId());
           if(manager.isPresent()){
               managerName = manager.get().getName();
           }
           Set<BatchModel.BatchSubject> subjects = batchModel.getSubjects();
           Set<String> subjectNames = new HashSet<>();
           for(BatchModel.BatchSubject batchSubject : subjects){
               Optional<SubjectModel> subjectModel = subjectRepository.findById(batchSubject.getSubjectId());
               if(subjectModel.isPresent()){
                   subjectNames.add(subjectModel.get().getName());
               }
           }
           responseDTOS.add(BatchInfoResponseDTO.builder()
               .name(batchModel.getName())
                   .managerName(managerName)
                   .subjects(subjectNames)
                   .startDate(batchModel.getStartDate())
                   .endDate(batchModel.getEndDate())
                   .numberOfStudents(batchModel.getStudentId().size())
                   .build()
               );
        }
        return responseDTOS;
    }


    private SubjectSelectionDTO mapToStudentViewDTO(
        BatchModel.BatchSubject batchSubject,
        SubjectModel subject
    ) {
        SubjectSelectionDTO dto = new SubjectSelectionDTO();
        dto.setSubjectId(subject.getId());
        dto.setSubjectName(subject.getName());

        Set<TopicSelectionDTO> topicDTOs = batchSubject.getSelectedTopics().stream()
            .map(selectedTopic -> {
                TopicModel topic = topicRepository.findById(selectedTopic.getTopicId())
                    .orElseThrow(() -> new ResourceNotFoundException("Topic not found"));
                TopicSelectionDTO topicDTO = new TopicSelectionDTO();
                topicDTO.setTopicId(topic.getId());
                topicDTO.setTopicName(topic.getName());
                topicDTO.setSelectedDate(selectedTopic.getSelectedDate());
                return topicDTO;
            })
            .collect(Collectors.toSet());
        dto.setTopics(topicDTOs);

        return dto;
    }

    private BatchModel.BatchSubject findOrCreateBatchSubject(BatchModel batch, String subjectId) {
        return batch.getSubjects().stream()
            .filter(bs -> bs.getSubjectId().equals(subjectId))
            .findFirst()
            .orElseGet(() -> {
                BatchModel.BatchSubject newBatchSubject = new BatchModel.BatchSubject();
                newBatchSubject.setSubjectId(subjectId);
                newBatchSubject.setSelectedTopics(new HashSet<>());
                batch.getSubjects().add(newBatchSubject);
                return newBatchSubject;
            });
    }

    private boolean isTopicSelected(BatchModel.BatchSubject batchSubject, String topicId) {
        for (BatchModel.SelectedTopic selectedTopic : batchSubject.getSelectedTopics()) {
            if (selectedTopic.getTopicId().equals(topicId)) {
                return true;
            }
        }
        return false;
    }

    private BatchModel.SelectedTopic findSelectedTopic(
        BatchModel.BatchSubject batchSubject,
        String topicId
    ) {
        for (BatchModel.SelectedTopic selectedTopic : batchSubject.getSelectedTopics()) {
            if (selectedTopic.getTopicId().equals(topicId)) {
                return selectedTopic;
            }
        }
        return null;
    }

    private void updateTopicSelections(BatchModel.BatchSubject batchSubject, Set<TopicUpdateDTO> updates) {
        for (TopicUpdateDTO update : updates) {
            if (update.isSelected()) {
                boolean exists = false;
                for (BatchModel.SelectedTopic st : batchSubject.getSelectedTopics()) {
                    if (st.getTopicId().equals(update.getTopicId())) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    BatchModel.SelectedTopic newTopic = new BatchModel.SelectedTopic();
                    newTopic.setTopicId(update.getTopicId());
                    newTopic.setSelectedDate(LocalDate.now());
                    batchSubject.getSelectedTopics().add(newTopic);
                }
            } else {
                Iterator<BatchModel.SelectedTopic> iterator = batchSubject.getSelectedTopics().iterator();
                while (iterator.hasNext()) {
                    BatchModel.SelectedTopic st = iterator.next();
                    if (st.getTopicId().equals(update.getTopicId())) {
                        iterator.remove();
                        break;
                    }
                }
            }
        }
    }

    private SubjectSelectionDTO mapToSelectionDTO(BatchModel batch, SubjectModel subject) {
        SubjectSelectionDTO dto = new SubjectSelectionDTO();
        dto.setSubjectId(subject.getId());
        dto.setSubjectName(subject.getName());

        // Fetch topics by subjectId (assuming topicRepository exists)
        List<TopicModel> topics = topicRepository.findBySubjectId(subject.getId());
        Set<TopicSelectionDTO> topicDTOs = new HashSet<>();
        for (TopicModel topic : topics) {
            topicDTOs.add(mapTopicSelectionDTO(batch, subject.getId(), topic));
        }
        dto.setTopics(topicDTOs);

        return dto;
    }

    private TopicSelectionDTO mapTopicSelectionDTO(
        BatchModel batch,
        String subjectId,
        TopicModel topic
    ) {
        Optional<BatchModel.SelectedTopic> selectedTopic = findSelectedTopic(batch, subjectId, topic.getId());
        TopicSelectionDTO dto = new TopicSelectionDTO();
        dto.setTopicId(topic.getId());
        dto.setTopicName(topic.getName());
        dto.setSelected(selectedTopic.isPresent());
        dto.setSelectedDate(selectedTopic.map(BatchModel.SelectedTopic::getSelectedDate).orElse(null));
        return dto;
    }

    private Optional<BatchModel.SelectedTopic> findSelectedTopic(BatchModel batch, String subjectId, String topicId) {
        for (BatchModel.BatchSubject subject : batch.getSubjects()) {
            if (subject.getSubjectId().equals(subjectId)) {
                for (BatchModel.SelectedTopic selectedTopic : subject.getSelectedTopics()) {
                    if (selectedTopic.getTopicId().equals(topicId)) {
                        return Optional.of(selectedTopic);
                    }
                }
                break;
            }
        }
        return Optional.empty();
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
