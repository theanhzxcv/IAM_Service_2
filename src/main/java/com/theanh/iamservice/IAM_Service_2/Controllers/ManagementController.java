package com.theanh.iamservice.IAM_Service_2.Controllers;

import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Management.UserCreationRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Management.UserSearchRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Management.UserUpdateRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Api.ApiResponse;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Api.ApiResponseBuilder;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Management.SearchResponse;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Management.UserResponse;
import com.theanh.iamservice.IAM_Service_2.Services.ServiceImp.ManagementImp.ManagementServiceImp;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "Management")
public class ManagementController {
    private final ManagementServiceImp managementServiceImp;

    @PreAuthorize("hasPermission('User','Create')")
    @PostMapping
    public ApiResponse<UserResponse> createNewUser(@ParameterObject @Valid UserCreationRequest userCreationRequest) {
        UserResponse newUser = managementServiceImp.createNewUser(userCreationRequest);

        return ApiResponseBuilder.createdSuccessResponse("New user created",
                newUser);
    }

    @PreAuthorize("hasPermission('User','Read')")
    @GetMapping
    public ApiResponse<Page<UserResponse>> allUsers(@RequestParam int page, @RequestParam int size) {
        Page<UserResponse> allUsers = managementServiceImp.allUsers(page, size);

        return ApiResponseBuilder.buildSuccessResponse("All users",
                allUsers);
    }

    @PreAuthorize("hasPermission('User','Read')")
    @GetMapping("/search")
    public ApiResponse<Page<SearchResponse>> findUserByKeyword(
            @ParameterObject UserSearchRequest userSearchRequest
    ) {
        Page<SearchResponse> userMatchedFound = managementServiceImp.findUserByKeyWord(userSearchRequest);

        if (userMatchedFound.isEmpty()) {
            return ApiResponseBuilder.buildSuccessResponse("No user found with keyword: "
                    + userSearchRequest.getKeyword(), userMatchedFound);
        }
        return ApiResponseBuilder.buildSuccessResponse("User found with keyword: "
                        + userSearchRequest.getKeyword(), userMatchedFound);
    }

    @PreAuthorize("hasPermission('User','Update')")
    @PutMapping
    public ApiResponse<UserResponse> updateUser(
            @ParameterObject @Valid UserUpdateRequest userUpdateRequest) {
        UserResponse updatedUser = managementServiceImp.updateUser(userUpdateRequest);

        return ApiResponseBuilder.buildSuccessResponse("User with email: "
                        + userUpdateRequest.getEmail()
                        + " updated",
                updatedUser);
    }

    @PutMapping("/password")
    public ApiResponse<String> changePassword(@RequestParam String email,
                                              @RequestParam String newPassword) {
        String changedPassword = managementServiceImp.changePassword(email, newPassword);

        return ApiResponseBuilder.buildSuccessResponse("Password changed",
                changedPassword);
    }

    @PreAuthorize("hasPermission('User','Delete')")
    @PutMapping("/ban/{email}")
    public ApiResponse<UserResponse> banUser(@PathVariable("email") String emailAddress) {
        UserResponse bannedUser = managementServiceImp.banUser(emailAddress);

        return ApiResponseBuilder.buildSuccessResponse("User banned",
                bannedUser);
    }

    @PreAuthorize("hasPermission('User','Delete')")
    @DeleteMapping("/{email}")
    public ApiResponse<UserResponse> deleteUser(@PathVariable("email") String emailAddress) {
        UserResponse deletedUser = managementServiceImp.deleteUser(emailAddress);

        return ApiResponseBuilder.buildSuccessResponse("User deleted",
                deletedUser);
    }
}
