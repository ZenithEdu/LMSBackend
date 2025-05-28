package com.MiniLms.LMSBackend.service.ContentService;

import com.MiniLms.LMSBackend.dto.RequestDTO.ContentRequestDTOs.ResourceRequestDTO;
import com.MiniLms.LMSBackend.dto.RequestDTO.ContentRequestDTOs.TopicRequestDTO;
import com.MiniLms.LMSBackend.dto.RequestDTO.ContentRequestDTOs.TopicUpdateRequestDTO;
import com.MiniLms.LMSBackend.dto.ResponseDTO.ContentResponseDTO.ResourceResponseDTO;
import com.MiniLms.LMSBackend.dto.ResponseDTO.ContentResponseDTO.TopicResponseDTO;
import com.MiniLms.LMSBackend.exceptions.DuplicateResourceException;
import com.MiniLms.LMSBackend.exceptions.ResourceNotFoundException;
import com.MiniLms.LMSBackend.model.ContentModels.Resource;
import com.MiniLms.LMSBackend.model.ContentModels.SubjectModel;
import com.MiniLms.LMSBackend.model.ContentModels.TopicModel;
import com.MiniLms.LMSBackend.repository.ContentRepositories.ISubjectRepository;
import com.MiniLms.LMSBackend.repository.ContentRepositories.ITopicRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class TopicServiceImpl implements ITopicService{

    private final ISubjectRepository subjectRepository;
    private final ITopicRepository topicRepository;
    private final GridFsTemplate gridFsTemplate;
    private final GridFsOperations gridFsOperations;


    @Autowired
    public TopicServiceImpl(
        ISubjectRepository subjectRepository,
        ITopicRepository topicRepository,
        GridFsTemplate gridFsTemplate,
        GridFsOperations gridFsOperations
    ){
        this.subjectRepository = subjectRepository;
        this.topicRepository = topicRepository;
        this.gridFsTemplate = gridFsTemplate;
        this.gridFsOperations = gridFsOperations;
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public TopicResponseDTO createTopic(TopicRequestDTO dto, String subjectId) throws IOException {

        Optional<SubjectModel> hasSubject = subjectRepository.findById(subjectId);
        if(hasSubject.isEmpty()){
            throw new ResourceNotFoundException("Subject Does not Exists with this id");
        }

        Optional<TopicModel> existingTopic = topicRepository.findByNameAndSubjectId(dto.getName(), subjectId);
        if (existingTopic.isPresent()) {
            throw new DuplicateResourceException("Topic with the same name already exists in this subject");
        }
        SubjectModel subjectModel = hasSubject.get();
        Resource resource = storeFiles(dto.getResourceRequestDTO());
        TopicModel topicModel = dto.toEntity(subjectId,resource);
        topicModel = topicRepository.save(topicModel);

        Set<String> updateTopicId = subjectModel.getTopicsIds();
        updateTopicId.add(topicModel.getId());
        subjectModel.setTopicsIds(updateTopicId);
        subjectRepository.save(subjectModel);

        return TopicResponseDTO.fromEntity(topicModel);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STUDENT')")
    public TopicResponseDTO getTopic(String id) {
        Optional<TopicModel> hasTopic = topicRepository.findById(id);
        if(hasTopic.isEmpty()){
            throw new RuntimeException("Topic not Found");
        }
        return TopicResponseDTO.fromEntity(hasTopic.get());
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public List<TopicResponseDTO> getTopicsBySubject(String subjectId) {
        Optional<SubjectModel> hasSubject = subjectRepository.findById(subjectId);
        if(hasSubject.isEmpty()){
            throw new ResourceNotFoundException("Subject with such id does not exists");
        }
        List<TopicModel> allTopics = topicRepository.findBySubjectId(subjectId);
        if(allTopics == null ||allTopics.isEmpty()){
            throw new RuntimeException("Subject has no topics");
        }
        List<TopicResponseDTO> ans = new ArrayList<>();
        for(TopicModel model : allTopics){
            ans.add(TopicResponseDTO.fromEntity(model));
        }
        return ans;
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public TopicResponseDTO updateTopic(String topicId, TopicUpdateRequestDTO dto) throws IOException {
       TopicModel existing = topicRepository.findById(topicId)
           .orElseThrow(() -> new ResourceNotFoundException("Topic not found"));

       if(dto.getName() != null){
           existing.setName(dto.getName());
       }

       if(dto.getResourceRequestDTO() != null){
           Resource existingRes = existing.getResource();
           ResourceRequestDTO requestDTO = dto.getResourceRequestDTO();

           if(existingRes == null) {
               existingRes = storeFiles(requestDTO);
           }else{
               if(requestDTO.getClassPPT() != null){
                   existingRes.setClassPPT(storeFile(requestDTO.getClassPPT()));
               }
               if(requestDTO.getExercise() != null){
                   existingRes.setExercise(storeFile(requestDTO.getExercise()));
               }
               if(requestDTO.getSolution() != null){
                   existingRes.setSolution(storeFile(requestDTO.getSolution()));
               }
               existingRes.setTest(requestDTO.getTest());
               existingRes.setVideo(requestDTO.getVideo());

               existing.setResource(existingRes);
           }
       }
       TopicModel model = topicRepository.save(existing);
       return TopicResponseDTO.fromEntity(model);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteTopic(String subjectId,String id) {
        Optional<SubjectModel> hasSubject = subjectRepository.findById(subjectId);
        if(hasSubject.isEmpty()){
            throw new ResourceNotFoundException("This subject does not exists");
        }
        SubjectModel subjectModel = hasSubject.get();
        Set<String> topicIds = subjectModel.getTopicsIds();
        if(topicIds.contains(id)){
            topicIds.remove(id);
        }
        subjectModel.setTopicsIds(topicIds);
        subjectRepository.save(subjectModel);

        TopicModel topic = topicRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Topic not found"));

        deleteFile(topic.getResource().getClassPPT());
        deleteFile(topic.getResource().getExercise());
        deleteFile(topic.getResource().getSolution());

        topicRepository.deleteById(id);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteAllBySubjectId(String subjectId){
        List<TopicModel> topics = topicRepository.findBySubjectId(subjectId);

        // Delete all topics and their subtopics
        for (TopicModel topic : topics) {
            String topicId = topic.getId();
            deleteTopic(subjectId, topicId);
        }

        // Clear subject's topic references
        Optional<SubjectModel> subject = subjectRepository.findById(subjectId);
        subject.ifPresent(sub -> {
            sub.getTopicsIds().clear();
            subjectRepository.save(sub);
        });
    }

    @Override
    public List<TopicResponseDTO> findAllBySubjectId(String subjectId) {
        Optional<SubjectModel> hasSubject = subjectRepository.findById(subjectId);
        if(hasSubject.isEmpty()){
            throw new ResourceNotFoundException("This subject does not exists");
        }
        SubjectModel subjectModel = hasSubject.get();
        Set<String> topicIds = subjectModel.getTopicsIds();
        List<TopicModel> allTopics = topicRepository.findBySubjectId(subjectId);
        List<TopicResponseDTO> ans = new ArrayList<>();
        for(TopicModel topicModel : allTopics){
            ans.add(TopicResponseDTO.fromEntity(topicModel));
        }
        return ans;
    }

    private void deleteFile(ObjectId fileId) {
        if(fileId != null) {
            gridFsTemplate.delete(new Query(Criteria.where("_id").is(fileId)));
        }
    }

    private Resource storeFiles(ResourceRequestDTO resourceRequestDTO) throws IOException{
        Resource resource = new Resource();
        if(resourceRequestDTO.getClassPPT() != null){
            resource.setClassPPT(storeFile(resourceRequestDTO.getClassPPT()));
        }
        if(resourceRequestDTO.getExercise() != null){
            resource.setExercise(storeFile(resourceRequestDTO.getExercise()));
        }
        if(resourceRequestDTO.getSolution() != null){
            resource.setSolution(storeFile(resourceRequestDTO.getSolution()));
        }
        resource.setTest(resourceRequestDTO.getTest());
        resource.setVideo(resourceRequestDTO.getVideo());
        return resource;
    }

    private ObjectId storeFile(MultipartFile file) throws IOException{
        return gridFsTemplate.store(
            file.getInputStream(),
            file.getOriginalFilename(),
            file.getContentType()
        );
    }
}
