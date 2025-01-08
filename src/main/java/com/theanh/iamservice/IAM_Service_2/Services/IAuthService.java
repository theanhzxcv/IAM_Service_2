package com.theanh.iamservice.IAM_Service_2.Services;

import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Authentication.SignInRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Authentication.SignOutRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Authentication.SignUpRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Authentication.VerificationRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Authentication.AuthResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface IAuthService {
    AuthResponse login(SignInRequest signInRequest, HttpServletRequest request);

    String registration(SignUpRequest signUpRequest, HttpServletRequest request);

    AuthResponse verification(VerificationRequest verificationRequest);

    AuthResponse refreshToken(String refreshToken);

    String logout(SignOutRequest signOutRequest, HttpServletRequest request);
}
