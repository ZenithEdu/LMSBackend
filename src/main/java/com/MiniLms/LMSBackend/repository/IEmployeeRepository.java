package com.MiniLms.LMSBackend.repository;

import com.MiniLms.LMSBackend.model.EmployeeModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface IEmployeeRepository extends MongoRepository<EmployeeModel,String> {
    Optional<EmployeeModel> findByEmail(String email);
    Optional<EmployeeModel> findByResetToken(String resetToken);
}
