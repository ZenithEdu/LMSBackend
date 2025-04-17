package com.MiniLms.LMSBackend.service.serviceImpl.registrationImpl;

import com.MiniLms.LMSBackend.dto.RequestDTO.EmployeeRegistrationRequestDTO;
import com.MiniLms.LMSBackend.dto.RequestDTO.StudentRegistrationRequestDTO;
import com.MiniLms.LMSBackend.dto.ResponseDTO.EmployeeRegistrationResponseDTO;
import com.MiniLms.LMSBackend.dto.ResponseDTO.StudentRegistrationResponseDTO;
import com.MiniLms.LMSBackend.dto.ResponseDTO.UserRegistrationResponseDTO;
import com.MiniLms.LMSBackend.dto.RequestDTO.UserRegistrationRequestDTO;
import com.MiniLms.LMSBackend.exceptions.UserAlreadyExistsException;
import com.MiniLms.LMSBackend.model.EmployeeModel;
import com.MiniLms.LMSBackend.model.StudentModel;
import com.MiniLms.LMSBackend.repository.IEmployeeRepository;
import com.MiniLms.LMSBackend.repository.IStudentRepository;
import com.MiniLms.LMSBackend.service.IRegistrationService;
import com.MiniLms.LMSBackend.service.emailService.IResetPasswordFirstTimeEmailService;
import com.MiniLms.LMSBackend.utils.PasswordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service("studentRegistrationService")
public class StudentRegistrationServiceImpl implements IRegistrationService {

    private final IStudentRepository studentRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final IResetPasswordFirstTimeEmailService resetPasswordFirstTimeEmailService;

    @Autowired
    public StudentRegistrationServiceImpl(
        IStudentRepository studentRepository,
        BCryptPasswordEncoder bCryptPasswordEncoder,
        IResetPasswordFirstTimeEmailService resetPasswordFirstTimeEmailService
    ){
        this.studentRepository = studentRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.resetPasswordFirstTimeEmailService = resetPasswordFirstTimeEmailService;
    }

    @Override
    public UserRegistrationResponseDTO register(UserRegistrationRequestDTO userRegistrationRequestDTO) throws RuntimeException {
        StudentRegistrationRequestDTO studentRegistrationRequestDto = (StudentRegistrationRequestDTO) userRegistrationRequestDTO;
        Optional<StudentModel> hasStudent = studentRepository.findByEmail(userRegistrationRequestDTO.getEmail());

        if(hasStudent.isPresent()){
            throw new UserAlreadyExistsException("User with email "+ studentRegistrationRequestDto.getEmail() +" already exists");
        }
        String password = PasswordGenerator.generateTemporaryPassword();
        String resetToken = generateResetToken();
        LocalDateTime tokenExpiry = LocalDateTime.now().plusHours(24);

        StudentModel studentModel = studentRegistrationRequestDto.toEntity();
        studentModel.setPassword(bCryptPasswordEncoder.encode(password));
        studentModel.setResetToken(resetToken);
        studentModel.setTokenExpiry(tokenExpiry);

        studentModel = studentRepository.save(studentModel);

        resetPasswordFirstTimeEmailService.sendResetEmail(studentModel.getEmail(),password,resetToken);
        System.out.println("Send Email");

        StudentRegistrationResponseDTO response = StudentRegistrationResponseDTO.fromEntity(studentModel);

        return response;
    }

    public List<StudentRegistrationResponseDTO> registerMultipleStudents(List<StudentRegistrationRequestDTO> studentRegistrationRequestDTOS){
        List<String> emails = studentRegistrationRequestDTOS.stream()
            .map(StudentRegistrationRequestDTO :: getEmail)
            .toList();

        List<StudentModel> existingStudents = studentRepository.findAllByEmailIn(emails);
        List<String> existingEmails = existingStudents.stream()
            .map(StudentModel::getEmail)
            .toList();

        List<StudentRegistrationRequestDTO> filteredDtos = studentRegistrationRequestDTOS.stream()
            .filter(dto -> !existingEmails.contains(dto.getEmail()))
            .toList();

        List<StudentWithPassword> newStudentsWithPasswords = filteredDtos.stream()
            .map(dto -> {
                String password = PasswordGenerator.generateTemporaryPassword();
                String resetToken = generateResetToken();
                LocalDateTime tokenExpiry = LocalDateTime.now().plusHours(24);

                StudentModel student = dto.toEntity();
                student.setPassword(bCryptPasswordEncoder.encode(password));
                student.setResetToken(resetToken);
                student.setTokenExpiry(tokenExpiry);

                return new StudentWithPassword(student,password);
            })
            .toList();

        List<StudentModel> newStudents = newStudentsWithPasswords.stream()
            .map(StudentWithPassword::getStudent)
            .toList();

        List<StudentModel> savedStudents = studentRepository.saveAll(newStudents);
        for (int i = 0; i < savedStudents.size(); i++) {
            StudentModel student = savedStudents.get(i);
            String plainPassword = newStudentsWithPasswords.get(i).getPlainPassword();

            resetPasswordFirstTimeEmailService.sendResetEmail(student.getEmail(), plainPassword, student.getResetToken());
        }

        return savedStudents.stream()
            .map(StudentRegistrationResponseDTO::fromEntity)
            .toList();
    }
}
class StudentWithPassword {
    private final StudentModel student;
    private final String plainPassword;

    public StudentWithPassword(StudentModel student, String plainPassword) {
        this.student = student;
        this.plainPassword = plainPassword;
    }

    public StudentModel getStudent() {
        return student;
    }

    public String getPlainPassword() {
        return plainPassword;
    }
}

