package com.MiniLms.LMSBackend.model.UserModelAndSubModels;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

@EqualsAndHashCode(callSuper = true)
@Document(collection = "student")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class StudentModel extends UserModel{
    private String uniId;
    private String branch;
    private String batchId;
}
