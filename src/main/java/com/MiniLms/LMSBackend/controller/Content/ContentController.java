package com.MiniLms.LMSBackend.controller.Content;

import com.MiniLms.LMSBackend.dto.RequestDTO.ContentRequestDTOs.SubjectRequestDTO;
import com.MiniLms.LMSBackend.dto.ResponseDTO.ContentResponseDTO.SubjectResponseDTO;
import com.MiniLms.LMSBackend.repository.ContentRepositories.ISubjectRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/content")
public class ContentController {

}
