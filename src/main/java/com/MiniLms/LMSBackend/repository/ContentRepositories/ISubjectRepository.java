package com.MiniLms.LMSBackend.repository.ContentRepositories;

import com.MiniLms.LMSBackend.model.ContentModels.SubjectModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ISubjectRepository extends MongoRepository<SubjectModel,String> {
    Optional<SubjectModel> findByName(String name);
}
