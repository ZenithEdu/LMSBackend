package com.MiniLms.LMSBackend.service.RegistrationService;

import java.util.UUID;

public interface IGenerateResetToken {
    default String generateResetToken(){
        return UUID.randomUUID().toString();
    }
}
