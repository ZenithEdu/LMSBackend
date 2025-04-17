package com.MiniLms.LMSBackend.service.serviceImpl.registrationImpl;

import com.MiniLms.LMSBackend.service.IRegistrationService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RegistrationServiceFactory {
    private final Map<RegistrationType, IRegistrationService> registrationServiceMap;

    public RegistrationServiceFactory(
        @Qualifier("employeeRegistrationService") IRegistrationService employeeRegistrationService,
        @Qualifier("studentRegistrationService") IRegistrationService studentRegistrationService
    ){
        registrationServiceMap = new HashMap<>();
        registrationServiceMap.put(RegistrationType.EMPLOYEE,employeeRegistrationService);
        registrationServiceMap.put(RegistrationType.STUDENT,studentRegistrationService);
    }

    public IRegistrationService getService(RegistrationType type){
        return registrationServiceMap.get(type);
    }
}
