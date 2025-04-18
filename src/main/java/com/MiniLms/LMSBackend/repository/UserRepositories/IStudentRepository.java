package com.MiniLms.LMSBackend.repository.UserRepositories;

import com.MiniLms.LMSBackend.model.UserModelAndSubModels.StudentModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface IStudentRepository extends MongoRepository<StudentModel,String> {
    Optional<StudentModel> findByEmail(String email);
    Optional<StudentModel> findByResetToken(String resetToken);
    List<StudentModel> findAllByEmailIn(List<String> emails);
}
