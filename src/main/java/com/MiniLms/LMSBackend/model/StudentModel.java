package com.MiniLms.LMSBackend.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Document(collection = "student")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class StudentModel extends UserModel{
    private String batchId;
}
