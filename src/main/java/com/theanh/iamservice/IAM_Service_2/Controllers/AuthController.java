package com.theanh.iamservice.IAM_Service_2.Controllers;

import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Authentication.SignInRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Authentication.SignOutRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Authentication.SignUpRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Api.ApiResponse;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Authentication.AuthResponse;
import com.theanh.iamservice.IAM_Service_2.Facatories.AuthServiceFactory;
import com.theanh.iamservice.IAM_Service_2.Services.IAuthService;
import com.theanh.iamservice.IAM_Service_2.Services.ServiceImp.AuthenticationImp.GoogleAuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Authentication")
public class AuthController {
    private final GoogleAuthService googleAuthService;
    private final AuthServiceFactory authServiceFactory;

    @GetMapping("/home")
    public ApiResponse<AuthResponse> googleLogin(@AuthenticationPrincipal OAuth2User user) {
        AuthResponse successful = googleAuthService.googleLogin(user);

        return ApiResponse.of(successful)
                .success("Welcome back! "
                        + user.getAttribute("given_name") + " "
                        + user.getAttribute("family_name"));
    }

    @PostMapping("/sign-in")
    public ApiResponse<AuthResponse> login(@ParameterObject @Valid SignInRequest signInRequest,
                                           HttpServletRequest request) {
        IAuthService authService = authServiceFactory.getAuthService();
        AuthResponse signedIn = authService.login(signInRequest, request);

        return ApiResponse.of(signedIn)
                .success("Sign in successfully, Welcome back!");
    }

    @PostMapping("/sign-up")
    private ApiResponse<String> registration(@ParameterObject @Valid SignUpRequest signUpRequest,
                                             HttpServletRequest request) {
        IAuthService authService = authServiceFactory.getAuthService();
        String signedUp = authService.registration(signUpRequest, request);

        return ApiResponse.of(signedUp).success(
                "Sign up successfully, Welcome to IAM!");
    }

    @PostMapping("/tokens/refresh")
    public ApiResponse<AuthResponse> refreshToken(@RequestParam String refreshToken)
            throws IOException {
        IAuthService authService = authServiceFactory.getAuthService();
        AuthResponse result = authService.refreshToken(refreshToken);

        return ApiResponse.of(result)
                .success("Token refreshed.");

    }

    @DeleteMapping("/logout")
    private ApiResponse<String> logout(@ParameterObject SignOutRequest signOutRequest,
                                       HttpServletRequest request) {
        IAuthService authService = authServiceFactory.getAuthService();
        String loggedOut = authService.logout(signOutRequest, request);

        return ApiResponse.of(loggedOut)
                .success("Logged out.");
    }
}
