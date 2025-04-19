package com.MiniLms.LMSBackend.dto.RequestDTO.ContentRequestDTOs.PDFValidations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class PDFFileValidator implements ConstraintValidator<PDFFile, MultipartFile> {
    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext constraintValidatorContext) {
        if (file == null || file.isEmpty()) {
            return true; // allow null or empty files
        }

        String contentType = file.getContentType();
        return contentType != null && contentType.equals("application/pdf");
    }
}
