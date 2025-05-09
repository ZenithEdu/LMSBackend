package com.MiniLms.LMSBackend.service.ContentService;

import com.MiniLms.LMSBackend.dto.RequestDTO.ContentRequestDTOs.SubjectRequestDTO;
import com.MiniLms.LMSBackend.dto.ResponseDTO.ContentResponseDTO.SubjectResponseDTO;
import com.MiniLms.LMSBackend.exceptions.ResourceNotFoundException;
import com.MiniLms.LMSBackend.exceptions.SubjectAlreadyExistsException;
import com.MiniLms.LMSBackend.model.ContentModels.SubjectModel;
import com.MiniLms.LMSBackend.repository.ContentRepositories.ISubjectRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SubjectServiceImpl implements ISubjectService{

    private final ISubjectRepository subjectRepository;
    private final ITopicService topicService;

    public SubjectServiceImpl(
        ISubjectRepository subjectRepository,
        ITopicService topicService
    ){
        this.subjectRepository = subjectRepository;
        this.topicService = topicService;
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public SubjectResponseDTO createSubject(SubjectRequestDTO subjectRequestDTO) {
        String name = subjectRequestDTO.getName();
        Optional<SubjectModel> hasSubject = subjectRepository.findByName(name);
        if(hasSubject.isPresent()){
            throw new SubjectAlreadyExistsException("This subject already exists : "+ name);
        }
        SubjectModel subjectModel = subjectRequestDTO.toEntity();
        subjectModel = subjectRepository.save(subjectModel);
        return SubjectResponseDTO.fromEntity(subjectModel);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<SubjectResponseDTO> getAllSubjects() {
        return subjectRepository.findAll()
            .stream()
            .map(SubjectResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STUDENT')")
    public SubjectResponseDTO getSubjectById(String id) {
        Optional<SubjectModel> hasSubject = subjectRepository.findById(id);
        if(hasSubject.isEmpty()){
            throw new ResourceNotFoundException("Subject not found with id: " + id);
        }
        return SubjectResponseDTO.fromEntity(hasSubject.get());
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public SubjectResponseDTO updateSubject(String id, SubjectRequestDTO dto) {
        SubjectModel existingSubject = subjectRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + id));

        existingSubject.setName(dto.getName());
        existingSubject.setDescription(dto.getDescription());

        SubjectModel updatedSubject = subjectRepository.save(existingSubject);
        return SubjectResponseDTO.fromEntity(updatedSubject);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteSubject(String id) {
        SubjectModel existingSubject = subjectRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + id));
        topicService.deleteAllBySubjectId(id);
        subjectRepository.delete(existingSubject);
    }
}
