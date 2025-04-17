package com.MiniLms.LMSBackend.service.serviceImpl.registrationImpl;

import com.MiniLms.LMSBackend.model.UserType;
import com.MiniLms.LMSBackend.service.IRegistrationService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RegistrationServiceFactory {
    private final Map<UserType, IRegistrationService> registrationServiceMap;

    public RegistrationServiceFactory(
        @Qualifier("employeeRegistrationService") IRegistrationService employeeRegistrationService,
        @Qualifier("studentRegistrationService") IRegistrationService studentRegistrationService
    ){
        registrationServiceMap = new HashMap<>();
        registrationServiceMap.put(UserType.EMPLOYEE,employeeRegistrationService);
        registrationServiceMap.put(UserType.STUDENT,studentRegistrationService);
    }

    public IRegistrationService getService(UserType type){
        return registrationServiceMap.get(type);
    }
}
