package com.MiniLms.LMSBackend.model.UserModelAndSubModels;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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

    @NotBlank(message = "Phone number is required")
    @Pattern(
        regexp = "^[6-9]\\d{9}$",
        message = "Phone number must be 10 digits and start with 6, 7, 8, or 9"
    )
    private String phone;

    @NotNull(message = "Gender is mandatory")
    private Gender gender;

    List<String> batchId;
}
