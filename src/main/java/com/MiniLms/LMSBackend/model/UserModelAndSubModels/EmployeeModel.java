package com.MiniLms.LMSBackend.model.UserModelAndSubModels;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Document(collection = "employee")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeModel extends UserModel {
    List<String> batchId;
}
