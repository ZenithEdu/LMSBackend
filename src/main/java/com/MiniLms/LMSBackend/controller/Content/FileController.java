package com.MiniLms.LMSBackend.controller.Content;

import com.MiniLms.LMSBackend.service.ContentService.IFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/content/files")
public class FileController {

    private final IFileService fileService;

    @Autowired
    public FileController(IFileService fileService){
        this.fileService = fileService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<InputStreamResource> getFile(@PathVariable String id) throws IOException{
        return fileService.getFileById(id);
    }
}
