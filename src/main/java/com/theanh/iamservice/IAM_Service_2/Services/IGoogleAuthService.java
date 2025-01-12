package com.theanh.iamservice.IAM_Service_2.Services;

import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Authentication.AuthResponse;
import org.springframework.security.oauth2.core.user.OAuth2User;

public interface IGoogleAuthService {

    AuthResponse googleLogin(OAuth2User user);
}
