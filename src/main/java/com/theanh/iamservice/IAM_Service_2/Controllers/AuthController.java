package com.theanh.iamservice.IAM_Service_2.Controllers;

import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Authentication.SignInRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Authentication.SignOutRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Authentication.SignUpRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Api.ApiResponse;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Api.ApiResponseBuilder;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Authentication.AuthResponse;
import com.theanh.iamservice.IAM_Service_2.Facatories.AuthServiceFactory;
import com.theanh.iamservice.IAM_Service_2.Services.IAuthService;
import com.theanh.iamservice.IAM_Service_2.Services.ServiceImp.AuthenticationImp.KeycloakAuthServiceImp;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Authentication")
public class AuthController {
    private final AuthServiceFactory authServiceFactory;

    @PostMapping("/sign-in")
    public ApiResponse<AuthResponse> login(@ParameterObject @Valid SignInRequest signInRequest,
                                           HttpServletRequest request) {
        IAuthService authService = authServiceFactory.getAuthService();
        AuthResponse signedIn = authService.login(signInRequest, request);

        return ApiResponseBuilder.buildSuccessResponse(
                "Sign in successfully, Welcome back!",
                signedIn);
    }

    @PostMapping("/sign-up")
    private ApiResponse<String> registration(@ParameterObject @Valid SignUpRequest signUpRequest,
                                             HttpServletRequest request) {
        IAuthService authService = authServiceFactory.getAuthService();
        String signedUp = authService.registration(signUpRequest, request);

        return ApiResponseBuilder.createdSuccessResponse(
                "Sign up successfully, Welcome to IAM!",
                signedUp);
    }

    @PostMapping("/tokens/refresh")
    public ApiResponse<AuthResponse> refreshToken(@RequestParam String refreshToken)
            throws IOException {
        IAuthService authService = authServiceFactory.getAuthService();
        AuthResponse result = authService.refreshToken(refreshToken);

        return ApiResponseBuilder
                .buildSuccessResponse("Token refreshed.", result);

    }

    @DeleteMapping("/logout")
    private ApiResponse<String> logout(@ParameterObject SignOutRequest signOutRequest,
                                       HttpServletRequest request) {
        IAuthService authService = authServiceFactory.getAuthService();
        String loggedOut = authService.logout(signOutRequest, request);

        return ApiResponseBuilder.buildSuccessResponse(
                "Log out successfully, See you later!",
                loggedOut);
    }
}
