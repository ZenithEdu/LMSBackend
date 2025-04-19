package com.MiniLms.LMSBackend.dto.RequestDTO.ContentRequestDTOs.PDFValidations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PDFFileValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface PDFFile {
    String message() default "Only PDF files are allowed";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
