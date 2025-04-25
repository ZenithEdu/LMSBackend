package com.MiniLms.LMSBackend.repository.BatchRepository;

import com.MiniLms.LMSBackend.model.BatchModels.BatchModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IBatchRepository extends MongoRepository<BatchModel,String>{
}
