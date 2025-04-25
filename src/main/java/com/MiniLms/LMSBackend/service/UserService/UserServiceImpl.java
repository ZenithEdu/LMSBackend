package com.MiniLms.LMSBackend.service.UserService;

import com.MiniLms.LMSBackend.model.UserModelAndSubModels.EmployeeModel;
import com.MiniLms.LMSBackend.model.UserModelAndSubModels.Role;
import com.MiniLms.LMSBackend.model.UserModelAndSubModels.StudentModel;
import com.MiniLms.LMSBackend.model.UserModelAndSubModels.UserModel;
import com.MiniLms.LMSBackend.repository.UserRepositories.IEmployeeRepository;
import com.MiniLms.LMSBackend.repository.UserRepositories.IStudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements IUserService{

    private final IEmployeeRepository employeeRepository;
    private final IStudentRepository studentRepository;

    @Autowired
    public UserServiceImpl(
        IEmployeeRepository employeeRepository,
        IStudentRepository studentRepository
    ){
        this.employeeRepository = employeeRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    public boolean verifyEmail(String email) {
        Optional<EmployeeModel> isEmployee = employeeRepository.findByEmail(email);
        if(isEmployee.isPresent()){
            return true;
        }
        Optional<StudentModel> isStudent = studentRepository.findByEmail(email);
        if(isStudent.isPresent()){
            return true;
        }
        return false;
    }

    @Override
    public boolean saveUser(UserModel userModel) {
        Role role = userModel.getRole();
        if(role.equals(Role.ADMIN) || role.equals(Role.MANAGER)){
            EmployeeModel employeeModel = (EmployeeModel) userModel;
            employeeRepository.save(employeeModel);
            return true;
        }else if(role.equals(Role.STUDENT)){
            StudentModel studentModel = (StudentModel) userModel;
            studentRepository.save(studentModel);
            return true;
        }
        return false;
    }

    @Override
    public Optional<UserModel> findByMail(String email) {
        Optional<EmployeeModel> isEmployee = employeeRepository.findByEmail(email);
        if(isEmployee.isPresent()){
            return Optional.of(isEmployee.get());
        }
        Optional<StudentModel> isStudent = studentRepository.findByEmail(email);
        if(isStudent.isPresent()){
           return Optional.of(isStudent.get());
        }
        return Optional.empty();
    }

    @Override
    public Optional<UserModel> findById(String id) {
        Optional<EmployeeModel> isEmployee = employeeRepository.findById(id);
        if(isEmployee.isPresent()){
            return Optional.of(isEmployee.get());
        }
        Optional<StudentModel> isStudent = studentRepository.findById(id);
        if(isStudent.isPresent()){
            return Optional.of(isStudent.get());
        }
        return Optional.empty();
    }
}
