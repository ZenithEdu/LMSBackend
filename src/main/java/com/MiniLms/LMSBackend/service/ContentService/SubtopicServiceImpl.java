package com.MiniLms.LMSBackend.service.ContentService;

import com.MiniLms.LMSBackend.dto.RequestDTO.ContentRequestDTOs.ResourceRequestDTO;
import com.MiniLms.LMSBackend.dto.RequestDTO.ContentRequestDTOs.SubtopicRequestDTO;
import com.MiniLms.LMSBackend.dto.ResponseDTO.ContentResponseDTO.ResourceResponseDTO;
import com.MiniLms.LMSBackend.dto.ResponseDTO.ContentResponseDTO.SubtopicResponseDTO;
import com.MiniLms.LMSBackend.exceptions.DuplicateResourceException;
import com.MiniLms.LMSBackend.exceptions.ResourceNotFoundException;
import com.MiniLms.LMSBackend.model.ContentModels.Resource;
import com.MiniLms.LMSBackend.model.ContentModels.SubtopicModel;
import com.MiniLms.LMSBackend.model.ContentModels.TopicModel;
import com.MiniLms.LMSBackend.repository.ContentRepositories.ISubtopicRepository;
import com.MiniLms.LMSBackend.repository.ContentRepositories.ITopicRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class SubtopicServiceImpl implements ISubtopicService{

    private final ITopicRepository topicRepository;
    private final ISubtopicRepository subtopicRepository;
    private final GridFsTemplate gridFsTemplate;
    private final GridFsOperations gridFsOperations;

    @Autowired
    public SubtopicServiceImpl(
        ITopicRepository topicRepository,
        ISubtopicRepository subtopicRepository,
        GridFsTemplate gridFsTemplate,
        GridFsOperations gridFsOperations
    ){
        this.topicRepository = topicRepository;
        this.subtopicRepository = subtopicRepository;
        this.gridFsOperations = gridFsOperations;
        this.gridFsTemplate = gridFsTemplate;
    }

    @Override
    public SubtopicResponseDTO createSubtopic(SubtopicRequestDTO subtopicDTO, String subjectId, String topicId) throws IOException {
        Optional<TopicModel> hasTopic = topicRepository.findById(topicId);
        if (hasTopic.isEmpty()) {
            throw new ResourceNotFoundException("Topic Does not Exist with this id");
        }

        // Check if topic belongs to the specified subject
        if (!hasTopic.get().getSubjectId().equals(subjectId)) {
            throw new ResourceNotFoundException("Topic does not belong to the specified subject");
        }

        Optional<SubtopicModel> existingSubtopic = subtopicRepository.findByNameAndTopicId(subtopicDTO.getName(), topicId);
        if (existingSubtopic.isPresent()) {
            throw new DuplicateResourceException("Subtopic with the same name already exists in this topic");
        }

        TopicModel topicModel = hasTopic.get();
        Resource resource = storeFiles(subtopicDTO.getResourceRequestDTO());
        SubtopicModel subtopicModel = subtopicDTO.toEntity(topicId, resource);
        subtopicModel = subtopicRepository.save(subtopicModel);

        // Update topic with new subtopic reference
        Set<String> updatedSubtopicIds = topicModel.getSubtopicIds();
        updatedSubtopicIds.add(subtopicModel.getId());
        topicModel.setSubtopicIds(updatedSubtopicIds);
        topicRepository.save(topicModel);

        return convertToDto(subtopicModel);
    }

    @Override
    public SubtopicResponseDTO getSubtopic(String id) {
        Optional<SubtopicModel> hasSubtopic = subtopicRepository.findById(id);
        if (hasSubtopic.isEmpty()) {
            throw new ResourceNotFoundException("Subtopic not Found");
        }
        return convertToDto(hasSubtopic.get());
    }

    @Override
    public SubtopicResponseDTO updateSubtopic(String id, SubtopicRequestDTO subtopicDTO) {
        Optional<SubtopicModel> existingSubtopic = subtopicRepository.findById(id);
        if (existingSubtopic.isEmpty()) {
            throw new ResourceNotFoundException("Subtopic not found");
        }

        SubtopicModel subtopic = existingSubtopic.get();
        subtopic.setName(subtopicDTO.getName());

        // Update other fields as needed
        Resource resource = subtopic.getResource();
        if (resource != null) {
            resource.setArticle(subtopicDTO.getResourceRequestDTO().getArticle());
            resource.setVideo(subtopicDTO.getResourceRequestDTO().getVideo());
            resource.setPractice(subtopicDTO.getResourceRequestDTO().getPractice());
            subtopic.setResource(resource);
        }

        SubtopicModel updatedSubtopic = subtopicRepository.save(subtopic);
        return convertToDto(updatedSubtopic);
    }

    @Override
    public List<SubtopicResponseDTO> findAllByTopicId(String subjectId, String topicId) {
        Optional<TopicModel> hasTopic = topicRepository.findById(topicId);
        if (hasTopic.isEmpty()) {
            throw new ResourceNotFoundException("Topic not found");
        }

        // Verify topic belongs to subject
        if (!hasTopic.get().getSubjectId().equals(subjectId)) {
            throw new ResourceNotFoundException("Topic does not belong to the specified subject");
        }

        List<SubtopicModel> allSubtopics = subtopicRepository.findByTopicId(topicId);
        if (allSubtopics.isEmpty()) {
            throw new ResourceNotFoundException("No subtopics found for this topic");
        }

        List<SubtopicResponseDTO> response = new ArrayList<>();
        for (SubtopicModel subtopic : allSubtopics) {
            response.add(convertToDto(subtopic));
        }
        return response;
    }

    @Override
    public void deleteSubtopic(String subjectId, String topicId, String id) {
        Optional<TopicModel> hasTopic = topicRepository.findById(topicId);
        if (hasTopic.isEmpty()) {
            throw new ResourceNotFoundException("Topic not found");
        }

        // Verify topic belongs to subject
        if (!hasTopic.get().getSubjectId().equals(subjectId)) {
            throw new ResourceNotFoundException("Topic does not belong to the specified subject");
        }

        TopicModel topic = hasTopic.get();
        Set<String> subtopicIds = topic.getSubtopicIds();
        if (subtopicIds.contains(id)) {
            subtopicIds.remove(id);
            topic.setSubtopicIds(subtopicIds);
            topicRepository.save(topic);
        }

        SubtopicModel subtopic = subtopicRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Subtopic not found"));

        // Delete associated files
        if (subtopic.getResource() != null) {
            deleteFile(subtopic.getResource().getAdditionalResources());
            deleteFile(subtopic.getResource().getExercise());
            deleteFile(subtopic.getResource().getSolution());
        }

        subtopicRepository.deleteById(id);
    }

    @Override
    public void deleteAllByTopicId(String topicId) {
        List<SubtopicModel> subtopics = subtopicRepository.findByTopicId(topicId);

        // Delete all subtopics and their resources
        for (SubtopicModel subtopic : subtopics) {
            // Delete associated files
            Resource resource = subtopic.getResource();
            if (resource != null) {
                deleteFile(resource.getAdditionalResources());
                deleteFile(resource.getExercise());
                deleteFile(resource.getSolution());
            }
            subtopicRepository.deleteById(subtopic.getId());
        }

        // Clear topic's subtopic references
        Optional<TopicModel> topic = topicRepository.findById(topicId);
        topic.ifPresent(t -> {
            t.getSubtopicIds().clear();
            topicRepository.save(t);
        });
    }

    private Resource storeFiles(ResourceRequestDTO resourceRequestDTO) throws IOException {
        Resource resource = new Resource();
        if (resourceRequestDTO.getAdditionalResources() != null) {
            resource.setAdditionalResources(storeFile(resourceRequestDTO.getAdditionalResources()));
        }
        if (resourceRequestDTO.getExercise() != null) {
            resource.setExercise(storeFile(resourceRequestDTO.getExercise()));
        }
        if (resourceRequestDTO.getSolution() != null) {
            resource.setSolution(storeFile(resourceRequestDTO.getSolution()));
        }
        resource.setPractice(resourceRequestDTO.getPractice());
        resource.setArticle(resourceRequestDTO.getArticle());
        resource.setVideo(resourceRequestDTO.getVideo());
        return resource;
    }
    private ObjectId storeFile(MultipartFile file) throws IOException {
        return gridFsTemplate.store(
            file.getInputStream(),
            file.getOriginalFilename(),
            file.getContentType()
        );
    }
    private void deleteFile(ObjectId fileId) {
        if (fileId != null) {
            gridFsTemplate.delete(new Query(Criteria.where("_id").is(fileId)));
        }
    }
    private SubtopicResponseDTO convertToDto(SubtopicModel subtopic) {
        return SubtopicResponseDTO.builder()
            .id(subtopic.getId())
            .name(subtopic.getName())
            .topicId(subtopic.getTopicId())
            .responseDTO(convertResourceToDto(subtopic.getResource()))
            .build();
    }

    private ResourceResponseDTO convertResourceToDto(Resource resource) {
        if (resource == null) {
            return null;
        }
        return ResourceResponseDTO.builder()
            .article(resource.getArticle())
            .video(resource.getVideo())
            .additionalResourcesUrl(getFileUrl(resource.getAdditionalResources()))
            .exerciseUrl(getFileUrl(resource.getExercise()))
            .solutionUrl(getFileUrl(resource.getSolution()))
            .practiceUrl(resource.getPractice())
            .build();
    }
    private String getFileUrl(ObjectId fileId) {
        return fileId != null ? "api/content/files/" + fileId.toString() : null;
    }
}
