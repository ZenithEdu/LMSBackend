package com.MiniLms.LMSBackend.repository.BatchRepository;

import com.MiniLms.LMSBackend.model.BatchModels.BatchModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IBatchRepository extends MongoRepository<BatchModel,String>{

    @Query("{ 'name': { $regex: ?0, $options: 'i' } }")
    Optional<BatchModel> findByNameIgnoreCase(String name);
    List<BatchModel> findByManagerId(String managerId);
    List<BatchModel> findByEndDateBefore(LocalDate date);
}
