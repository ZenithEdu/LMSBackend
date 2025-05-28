package com.MiniLms.LMSBackend.model.ContentModels;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.bson.types.ObjectId;
import org.hibernate.validator.constraints.URL;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Resource {

    private ObjectId exercise;

    private ObjectId solution;

    @URL(message = "Invalid video URL")
    private String video;

    private ObjectId classPPT;

    @URL(message = "Invalid test URL")
    private String test;

}
