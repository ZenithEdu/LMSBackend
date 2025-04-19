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
    @URL(message = "Invalid article URL")
    private String article;

    @URL(message = "Invalid video URL")
    private String video;

    private ObjectId additionalResources;

    private ObjectId exercise;

    private ObjectId solution;

    @URL(message = "Invalid practice URL")
    private String practice;
}
