package com.theanh.iamservice.IAM_Service_2.Facatories;

import com.theanh.iamservice.IAM_Service_2.Services.IAuthService;
import com.theanh.iamservice.IAM_Service_2.Services.ServiceImp.AuthenticationImp.ApplicationAuthServiceImp;
import com.theanh.iamservice.IAM_Service_2.Services.ServiceImp.AuthenticationImp.KeycloakAuthServiceImp;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AuthServiceFactory {
    @Value("${keycloak.enabled}")
    private boolean isKeycloakEnabled;

    private final KeycloakAuthServiceImp keycloakAuthService;
    private final ApplicationAuthServiceImp applicationAuthService;

    public AuthServiceFactory(KeycloakAuthServiceImp keycloakAuthService, ApplicationAuthServiceImp applicationAuthService) {
        this.keycloakAuthService = keycloakAuthService;
        this.applicationAuthService = applicationAuthService;
    }

    public IAuthService getAuthService() {
        if (isKeycloakEnabled) {
            return keycloakAuthService;
        } else {
            return applicationAuthService;
        }
    }
}
