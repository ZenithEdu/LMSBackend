package com.MiniLms.LMSBackend.dto.RequestDTO.ContentRequestDTOs;

import com.MiniLms.LMSBackend.dto.RequestDTO.ContentRequestDTOs.PDFValidations.PDFFile;
import lombok.Data;
import org.hibernate.validator.constraints.URL;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ResourceRequestDTO {

    @PDFFile
    private MultipartFile exercise;

    @PDFFile
    private MultipartFile solution;

    @URL(message = "Video must be a valid URL")
    private String video;

    @PDFFile
    private MultipartFile classPPT;

    @URL(message = "Article must be a valid URL")
    private String article;
}
