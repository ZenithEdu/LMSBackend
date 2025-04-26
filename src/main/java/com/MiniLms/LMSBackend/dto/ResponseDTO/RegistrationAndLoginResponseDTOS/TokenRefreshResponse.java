package com.MiniLms.LMSBackend.dto.ResponseDTO.RegistrationAndLoginResponseDTOS;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenRefreshResponse {
    private String accessToken;
}
