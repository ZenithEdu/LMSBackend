package com.MiniLms.LMSBackend.service.UserService;

import com.MiniLms.LMSBackend.dto.ResponseDTO.RegistrationAndLoginResponseDTOS.UserRegistrationResponseDTO;
import com.MiniLms.LMSBackend.model.UserModelAndSubModels.*;
import com.MiniLms.LMSBackend.repository.UserRepositories.IEmployeeRepository;
import com.MiniLms.LMSBackend.repository.UserRepositories.IStudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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

    @Override
    public void deleteUser(String userId) {
        Optional<UserModel> findUser = findById(userId);
        if(findUser.isPresent()){
            UserModel userModel = findUser.get();
            if(userModel.getRole().equals(Role.STUDENT)){
                studentRepository.deleteById(userId);
            }else{
                employeeRepository.deleteById(userId);
            }
        }
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Long studentCount() {
        return studentRepository.count();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Long employeeCount() {
        return employeeRepository.count();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteEmployee(String id) {
        deleteUser(id);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserRegistrationResponseDTO> findAllByRole(String filterRole) {
        if (!"ADMIN".equalsIgnoreCase(filterRole) && !"MANAGER".equalsIgnoreCase(filterRole)) {
            throw new IllegalArgumentException("Invalid role filter. Allowed values: ADMIN, MANAGER");
        }
        List<EmployeeModel> employeeModels = null;
        if("ADMIN".equalsIgnoreCase(filterRole)){
            employeeModels = employeeRepository.findByRoleIgnoreCase(Role.ADMIN);
        }else{
            employeeModels = employeeRepository.findByRoleIgnoreCase(Role.MANAGER);
        }
        List<UserRegistrationResponseDTO> ans = new ArrayList<>();
        for(EmployeeModel model : employeeModels){
            ans.add(convertToDTO(model));
        }
        return ans;
    }

    private UserRegistrationResponseDTO convertToDTO(EmployeeModel model){
        return UserRegistrationResponseDTO.builder()
            .id(model.getId())
            .name(model.getName())
            .email(model.getEmail())
            .phone(model.getPhone())
            .role(model.getRole())
            .gender(model.getGender())
            .type(UserType.EMPLOYEE)
            .build();
    }
}
