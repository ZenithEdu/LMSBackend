package com.MiniLms.LMSBackend.dto.RequestDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordFirstTimeRequestDTO extends PasswordResetRequestDTO{
    @NotBlank(message = "Old password must not be blank")
    @Size(min = 8, message = "Old password must be at least 8 characters")
    private String oldPassword;
}
