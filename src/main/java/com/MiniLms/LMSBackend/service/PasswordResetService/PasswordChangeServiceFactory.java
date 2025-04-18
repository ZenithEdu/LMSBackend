package com.MiniLms.LMSBackend.service.PasswordResetService;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class PasswordChangeServiceFactory {
    private final Map<PasswordChange, IPasswordResetService> resetServiceMap;

    public PasswordChangeServiceFactory(
        @Qualifier("forgotPasswordService") IPasswordResetService forgotPasswordService,
        @Qualifier("passwordResetFirstTimeService") IPasswordResetService passwordResetFirstTimeService
    ) {
        resetServiceMap = new HashMap<>();
        resetServiceMap.put(PasswordChange.FORGOT_PASSWORD,forgotPasswordService);
        resetServiceMap.put(PasswordChange.REGISTRATION, passwordResetFirstTimeService);
    }
    public IPasswordResetService getService(PasswordChange passwordChange){
        return resetServiceMap.get(passwordChange);
    }
}
