package com.MiniLms.LMSBackend.service.ContentService;

import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.io.FileNotFoundException;
import java.io.IOException;



@Service
public class FileService implements IFileService{

    private final GridFsOperations gridFsOperations;
    private final GridFsTemplate gridFsTemplate;

    @Autowired
    public FileService(
        GridFsTemplate gridFsTemplate,
        GridFsOperations gridFsOperations
    ){
        this.gridFsOperations = gridFsOperations;
        this.gridFsTemplate = gridFsTemplate;
    }

    @Override
    //@PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MANAGER','ROLE_STUDENT')")
    public ResponseEntity<InputStreamResource> getFileById(String id) throws IOException {
        GridFSFile file = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(new ObjectId(id))));

        if (file == null) {
            throw new FileNotFoundException("File not found with id: " + id);
        }

        GridFsResource resource = gridFsOperations.getResource(file);

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(
                file.getMetadata() != null && file.getMetadata().get("_contentType") != null
                    ? file.getMetadata().get("_contentType").toString()
                    : MediaType.APPLICATION_OCTET_STREAM_VALUE
            ))
            .header("Content-Disposition", "inline; filename=\"" + file.getFilename() + "\"")
            .body(new InputStreamResource(resource.getInputStream()));
    }
}
