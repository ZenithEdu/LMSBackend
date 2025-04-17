package com.MiniLms.LMSBackend.dto.RequestDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequestDTO {

    @NotBlank(message = "Token must not be blank")
    private String token;

    @NotBlank(message = "Old password must not be blank")
    @Size(min = 8, message = "Old password must be at least 8 characters")
    private String oldPassword;

    @NotBlank(message = "New password must not be blank")
    @Size(min = 8, message = "New password must be at least 8 characters")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,}$",
        message = "Password must be at least 8 characters with at least one letter and one number")
    private String newPassword;
}
