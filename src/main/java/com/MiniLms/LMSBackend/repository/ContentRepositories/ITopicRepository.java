package com.MiniLms.LMSBackend.repository.ContentRepositories;

import com.MiniLms.LMSBackend.model.ContentModels.TopicModel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ITopicRepository extends MongoRepository<TopicModel,String> {
    List<TopicModel> findBySubjectId(String subjectId);
    Optional<TopicModel> findByNameAndSubjectId(String name, String subjectId);
}
