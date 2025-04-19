package com.MiniLms.LMSBackend.service.ContentService;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface IFileService {
    ResponseEntity<InputStreamResource> getFileById(String id) throws IOException;
}
