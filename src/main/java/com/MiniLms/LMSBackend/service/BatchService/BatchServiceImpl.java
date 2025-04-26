package com.MiniLms.LMSBackend.service.BatchService;

import com.MiniLms.LMSBackend.dto.ManagerBatchUpdateDTO.*;
import com.MiniLms.LMSBackend.dto.RequestDTO.BatchRequestDTOs.BatchCreationRequestDTO;
import com.MiniLms.LMSBackend.dto.RequestDTO.RegistrationAndLoginRequestDTOS.StudentRegistrationRequestDTO;
import com.MiniLms.LMSBackend.dto.ResponseDTO.BatchResponseDTOs.BatchCreationResponseDTO;
import com.MiniLms.LMSBackend.dto.ResponseDTO.ContentResponseDTO.SubjectResponseDTO;
import com.MiniLms.LMSBackend.dto.ResponseDTO.RegistrationAndLoginResponseDTOS.StudentRegistrationResponseDTO;
import com.MiniLms.LMSBackend.dto.ResponseDTO.RegistrationAndLoginResponseDTOS.UserRegistrationResponseDTO;
import com.MiniLms.LMSBackend.exceptions.ResourceNotFoundException;
import com.MiniLms.LMSBackend.model.BatchModels.BatchModel;
import com.MiniLms.LMSBackend.model.ContentModels.SubjectModel;
import com.MiniLms.LMSBackend.model.ContentModels.SubtopicModel;
import com.MiniLms.LMSBackend.model.ContentModels.TopicModel;
import com.MiniLms.LMSBackend.model.UserModelAndSubModels.UserModel;
import com.MiniLms.LMSBackend.model.UserModelAndSubModels.UserType;
import com.MiniLms.LMSBackend.repository.BatchRepository.IBatchRepository;
import com.MiniLms.LMSBackend.repository.ContentRepositories.ISubjectRepository;
import com.MiniLms.LMSBackend.repository.ContentRepositories.ISubtopicRepository;
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
    private final ISubtopicRepository subtopicRepository;
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
        ISubtopicRepository subtopicRepository,
        RegistrationServiceFactory registrationServiceFactory,
        BatchProcessService batchProcessService
    ){
        this.userService = userService;
        this.subjectService = subjectService;
        this.batchRepository = batchRepository;
        this.subjectRepository = subjectRepository;
        this.topicRepository = topicRepository;
        this.subtopicRepository = subtopicRepository;
        this.registrationServiceFactory = registrationServiceFactory;
        this.batchProcessService = batchProcessService;
    }


    @Override
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
    public BatchCreationResponseDTO getBatchById(String batchId) {
        Optional<BatchModel> hasBatch = batchRepository.findById(batchId);
        if(hasBatch.isEmpty()){
            throw new ResourceNotFoundException("No Such batch Exists with Id : " + batchId);
        }
        return mapToResponseDTO(hasBatch.get());
    }

    @Override
    public List<BatchCreationResponseDTO> getAllBatchesForManager(String managerId) {
        List<BatchModel> allBatches = batchRepository.findByManagerId(managerId);
        List<BatchCreationResponseDTO> responseDTOS = new ArrayList<>();
        for(BatchModel batchModel : allBatches){
            responseDTOS.add(mapToResponseDTO(batchModel));
        }
        return responseDTOS;
    }

    @Override
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
    public SubjectSelectionDTO getSubjectSelections(String batchId, String subjectId) {
        BatchModel batch = batchRepository.findById(batchId)
            .orElseThrow(() -> new ResourceNotFoundException("Batch not found"));

        SubjectModel subject = subjectRepository.findById(subjectId)
            .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));

        return mapToSelectionDTO(batch, subject);
    }

    @Override
    public void updateSelections(String batchId, String subjectId, BatchSelectionsUpdateRequest request) {
        BatchModel batch = batchRepository.findById(batchId)
            .orElseThrow(() -> new ResourceNotFoundException("Batch not found"));

        // Find or create BatchSubject
        BatchModel.BatchSubject batchSubject = null;
        for (BatchModel.BatchSubject bs : batch.getSubjects()) {
            if (bs.getSubjectId().equals(subjectId)) {
                batchSubject = bs;
                break;
            }
        }

        if (batchSubject == null) {
            batchSubject = new BatchModel.BatchSubject();
            batchSubject.setSubjectId(subjectId);
            batch.getSubjects().add(batchSubject);
        }

        updateTopicSelections(batchSubject, request.getTopicUpdates());
        updateSubtopicSelections(batchSubject, request.getSubtopicUpdates());

        batchRepository.save(batch);
    }

    @Override
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
    private SubjectSelectionDTO mapToStudentViewDTO(
        BatchModel.BatchSubject batchSubject,
        SubjectModel subject
    ) {
        SubjectSelectionDTO dto = new SubjectSelectionDTO();
        dto.setSubjectId(subject.getId());
        dto.setSubjectName(subject.getName());

        Set<TopicSelectionDTO> topicDTOs = new HashSet<>();
        List<TopicModel> topics = topicRepository.findBySubjectId(subject.getId());

        for (TopicModel topic : topics) {
            if (isTopicSelected(batchSubject, topic.getId())) {
                topicDTOs.add(mapTopicDTO(batchSubject, topic));
            }
        }

        dto.setTopics(topicDTOs);
        return dto;
    }

    private boolean isTopicSelected(BatchModel.BatchSubject batchSubject, String topicId) {
        for (BatchModel.SelectedTopic selectedTopic : batchSubject.getSelectedTopics()) {
            if (selectedTopic.getTopicId().equals(topicId)) {
                return true;
            }
        }
        return false;
    }

    private TopicSelectionDTO mapTopicDTO(
        BatchModel.BatchSubject batchSubject,
        TopicModel topic
    ) {
        TopicSelectionDTO topicDTO = new TopicSelectionDTO();
        topicDTO.setTopicId(topic.getId());
        topicDTO.setTopicName(topic.getName());

        BatchModel.SelectedTopic selectedTopic = findSelectedTopic(batchSubject, topic.getId());
        topicDTO.setSelectedDate(selectedTopic != null ? selectedTopic.getSelectedDate() : null);

        Set<SubtopicSelectionDTO> subtopicDTOs = new HashSet<>();
        for (String subtopicId : topic.getSubtopicIds()) {
            SubtopicModel subtopic = subtopicRepository.findById(subtopicId)
                .orElseThrow(() -> new ResourceNotFoundException("Subtopic not found"));

            if (isSubtopicSelected(selectedTopic, subtopic.getId())) {
                subtopicDTOs.add(mapSubtopicDTO(selectedTopic, subtopic));
            }
        }

        topicDTO.setSubtopics(subtopicDTOs);
        return topicDTO;
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

    private boolean isSubtopicSelected(
        BatchModel.SelectedTopic selectedTopic,
        String subtopicId
    ) {
        if (selectedTopic == null) return false;

        for (BatchModel.SelectedSubtopic ss : selectedTopic.getSelectedSubtopics()) {
            if (ss.getSubtopicId().equals(subtopicId)) {
                return true;
            }
        }
        return false;
    }

    private SubtopicSelectionDTO mapSubtopicDTO(
        BatchModel.SelectedTopic selectedTopic,
        SubtopicModel subtopic
    ) {
        SubtopicSelectionDTO subtopicDTO = new SubtopicSelectionDTO();
        subtopicDTO.setSubtopicId(subtopic.getId());
        subtopicDTO.setSubtopicName(subtopic.getName());

        LocalDate selectedDate = null;
        if (selectedTopic != null) {
            for (BatchModel.SelectedSubtopic ss : selectedTopic.getSelectedSubtopics()) {
                if (ss.getSubtopicId().equals(subtopic.getId())) {
                    selectedDate = ss.getSelectedDate();
                    break;
                }
            }
        }

        subtopicDTO.setSelectedDate(selectedDate);
        return subtopicDTO;
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
                    newTopic.setSelectedSubtopics(new HashSet<>());
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

    private void updateSubtopicSelections(BatchModel.BatchSubject batchSubject, Set<SubtopicUpdateDTO> updates) {
        for (SubtopicUpdateDTO update : updates) {
            for (BatchModel.SelectedTopic topic : batchSubject.getSelectedTopics()) {
                if (update.isSelected()) {
                    boolean exists = false;
                    for (BatchModel.SelectedSubtopic ss : topic.getSelectedSubtopics()) {
                        if (ss.getSubtopicId().equals(update.getSubtopicId())) {
                            exists = true;
                            break;
                        }
                    }
                    if (!exists) {
                        BatchModel.SelectedSubtopic newSS = new BatchModel.SelectedSubtopic();
                        newSS.setSubtopicId(update.getSubtopicId());
                        newSS.setSelectedDate(LocalDate.now());
                        topic.getSelectedSubtopics().add(newSS);
                    }
                } else {
                    Iterator<BatchModel.SelectedSubtopic> iterator = topic.getSelectedSubtopics().iterator();
                    while (iterator.hasNext()) {
                        BatchModel.SelectedSubtopic ss = iterator.next();
                        if (ss.getSubtopicId().equals(update.getSubtopicId())) {
                            iterator.remove();
                            break;
                        }
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

    private TopicSelectionDTO mapTopicSelectionDTO(BatchModel batch, String subjectId, TopicModel topic) {
        Optional<BatchModel.SelectedTopic> selectedTopic = findSelectedTopic(batch, subjectId, topic.getId());
        TopicSelectionDTO dto = new TopicSelectionDTO();
        dto.setTopicId(topic.getId());
        dto.setTopicName(topic.getName());
        dto.setSelected(selectedTopic.isPresent());
        dto.setSelectedDate(selectedTopic.map(BatchModel.SelectedTopic::getSelectedDate).orElse(null));

        // Process subtopics from topic's subtopicIds
        Set<SubtopicSelectionDTO> subtopicDTOs = new HashSet<>();
        for (String subtopicId : topic.getSubtopicIds()) {
            SubtopicModel subtopic = subtopicRepository.findById(subtopicId)
                .orElseThrow(() -> new ResourceNotFoundException("Subtopic not found: " + subtopicId));
            subtopicDTOs.add(mapSubtopicSelectionDTO(selectedTopic, subtopic));
        }
        dto.setSubtopics(subtopicDTOs);

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

    private SubtopicSelectionDTO mapSubtopicSelectionDTO(Optional<BatchModel.SelectedTopic> selectedTopicOpt, SubtopicModel subtopic) {
        SubtopicSelectionDTO dto = new SubtopicSelectionDTO();
        dto.setSubtopicId(subtopic.getId());
        dto.setSubtopicName(subtopic.getName());

        boolean selected = false;
        LocalDate selectedDate = null;

        if (selectedTopicOpt.isPresent()) {
            BatchModel.SelectedTopic selectedTopic = selectedTopicOpt.get();
            for (BatchModel.SelectedSubtopic ss : selectedTopic.getSelectedSubtopics()) {
                if (ss.getSubtopicId().equals(subtopic.getId())) {
                    selected = true;
                    selectedDate = ss.getSelectedDate();
                    break;
                }
            }
        }

        dto.setSelected(selected);
        dto.setSelectedDate(selectedDate);
        return dto;
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
