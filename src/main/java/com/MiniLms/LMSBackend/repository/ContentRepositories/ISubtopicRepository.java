package com.MiniLms.LMSBackend.repository.ContentRepositories;

import com.MiniLms.LMSBackend.model.ContentModels.SubjectModel;
import com.MiniLms.LMSBackend.model.ContentModels.SubtopicModel;
import com.MiniLms.LMSBackend.model.ContentModels.TopicModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ISubtopicRepository extends MongoRepository<SubtopicModel,String> {
    List<SubtopicModel> findByTopicId(String topicId);
    Optional<SubtopicModel> findByNameAndTopicId(String name, String topicId);
}
