package com.theanh.iamservice.IAM_Service_2.Controllers;

import com.theanh.iamservice.IAM_Service_2.Dtos.Request.User.PasswordChangeRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.User.PasswordResetRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.User.ProfileUpdateRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Api.ApiResponse;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.User.ProfileResponse;
import com.theanh.iamservice.IAM_Service_2.Services.ServiceImp.UserImp.UserServiceImp;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile")
@Tag(name = "User")
public class UserController {

    private final UserServiceImp userServiceImp;

    @GetMapping
    public ApiResponse<ProfileResponse> myProfile() {
        ProfileResponse myProfile = userServiceImp.myProfile();

        return ApiResponse.of(myProfile)
                .success(myProfile.getUsername() + "'s profile.");
    }

    @PutMapping
    public ApiResponse<ProfileResponse> updateProfile(
            @ParameterObject ProfileUpdateRequest profileUpdateRequest) {
        ProfileResponse profileUpdated = userServiceImp.updateProfile(profileUpdateRequest);

        return ApiResponse.of(profileUpdated)
                .success(profileUpdated.getUsername() + "'s profile updated");
    }

    @PutMapping("/password")
    public ApiResponse<String> changePassword(
            @ParameterObject PasswordChangeRequest passwordChangeRequest) {
        String passwordChanged = userServiceImp.changePassword(passwordChangeRequest);

        return ApiResponse.of(passwordChanged)
                .success("Your password changed");
    }

    @PostMapping("/password/forgot")
    public ApiResponse<String> forgotPassword(@RequestParam String email) {
        String forgotPassword = userServiceImp.forgotPassword(email);

        return ApiResponse.of(forgotPassword)
                .success("Password reset email sent.");
    }

    @PutMapping("/password/reset")
    public ApiResponse<String> resetPassword(
            @ParameterObject PasswordResetRequest passwordResetRequest) {
        String resetPassword = userServiceImp.resetPassword(passwordResetRequest);

        return ApiResponse.of(resetPassword)
                .success("Your password reset");
    }
}
