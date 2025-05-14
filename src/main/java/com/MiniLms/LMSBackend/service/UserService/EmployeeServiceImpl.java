package com.MiniLms.LMSBackend.service.UserService;

import com.MiniLms.LMSBackend.dto.RequestDTO.RegistrationAndLoginRequestDTOS.EmployeeRegistrationRequestDTO;
import com.MiniLms.LMSBackend.dto.ResponseDTO.RegistrationAndLoginResponseDTOS.EmployeeRegistrationResponseDTO;
import com.MiniLms.LMSBackend.model.UserModelAndSubModels.EmployeeModel;
import com.MiniLms.LMSBackend.repository.UserRepositories.IEmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EmployeeServiceImpl implements IEmployeeService{

    private final IEmployeeRepository employeeRepository;

    @Autowired
    public EmployeeServiceImpl(
        IEmployeeRepository employeeRepository
    ){
        this.employeeRepository = employeeRepository;
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER'")
    public EmployeeRegistrationResponseDTO updateEmployee(String id,EmployeeRegistrationRequestDTO employeeRegistrationRequestDTO) {
        EmployeeModel existingEmployee = employeeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));

        // 2. Validate email uniqueness if changing
        String newEmail = employeeRegistrationRequestDTO.getEmail();
        if (!newEmail.equalsIgnoreCase(existingEmployee.getEmail())) {
            if (employeeRepository.existsByEmailIgnoreCase(newEmail)) {
                throw new IllegalArgumentException("Email already in use by another employee");
            }
        }

        // 3. Validate phone uniqueness if changing
        String newPhone = employeeRegistrationRequestDTO.getPhone();
        if (!newPhone.equals(existingEmployee.getPhone())) {
            if (employeeRepository.existsByPhone(newPhone)) {
                throw new IllegalArgumentException("Phone number already in use by another employee");
            }
        }

        // 4. Update fields
        existingEmployee.setName(employeeRegistrationRequestDTO.getName());
        existingEmployee.setEmail(newEmail);
        existingEmployee.setPhone(newPhone);
        existingEmployee.setRole(employeeRegistrationRequestDTO.getRole());
        existingEmployee.setGender(employeeRegistrationRequestDTO.getGender());
        existingEmployee.setBatchId(employeeRegistrationRequestDTO.getBatchId());

        // 5. Save updated entity
        EmployeeModel updatedEmployee = employeeRepository.save(existingEmployee);

        // 6. Convert to response DTO
        return EmployeeRegistrationResponseDTO.fromEntity(updatedEmployee);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<EmployeeRegistrationResponseDTO> getAllEmployees() {
        List<EmployeeModel> allEmployee = employeeRepository.findAll();
        List<EmployeeRegistrationResponseDTO> responseDTOS = new ArrayList<>();
        for(EmployeeModel employeeModel : allEmployee){
            responseDTOS.add(EmployeeRegistrationResponseDTO.fromEntity(employeeModel));
        }
        return responseDTOS;
    }
}
