package com.MiniLms.LMSBackend.model.UserModelAndSubModels;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserModel {

    @Id
    private String id;

    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(
        regexp = "^[6-9]\\d{9}$",
        message = "Phone number must be 10 digits and start with 6, 7, 8, or 9"
    )
    private String phone;

    @NotBlank(message = "Password is mandatory")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,}$",
        message = "Password must be at least 8 characters with at least one letter and one number")
    private String password;

    @Builder.Default
    private boolean passwordChanged = false;

    private String resetToken;
    private LocalDateTime tokenExpiry;

    @NotNull(message = "Role is mandatory")
    private Role role;

    @NotNull(message = "Gender is mandatory")
    private Gender gender;

}
