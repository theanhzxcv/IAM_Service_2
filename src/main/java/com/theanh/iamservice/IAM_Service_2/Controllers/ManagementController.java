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
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "Management")
public class ManagementController {
    private final ManagementServiceImp managementServiceImp;

    @PostMapping
    public ApiResponse<UserResponse> createNewUser(@ParameterObject UserCreationRequest userCreationRequest) {
        UserResponse newUser = managementServiceImp.createNewUser(userCreationRequest);

        return ApiResponseBuilder.createdSuccessResponse("New user created",
                newUser);
    }

    @GetMapping
    public ApiResponse<Page<UserResponse>> allUsers(@RequestParam int page, @RequestParam int size) {
        Page<UserResponse> allUsers = managementServiceImp.allUsers(page, size);

        return ApiResponseBuilder.buildSuccessResponse("All users",
                allUsers);
    }

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

    @PatchMapping("/{email}")
    public ApiResponse<UserResponse> updateUser(
            @PathVariable("email") String emailAddress,
            @ParameterObject UserUpdateRequest userUpdateRequest) {
        UserResponse updatedUser = managementServiceImp.updateUser(emailAddress, userUpdateRequest);

        return ApiResponseBuilder.buildSuccessResponse("User with email: " + emailAddress + " updated",
                updatedUser);
    }

    @PutMapping("/ban/{email}")
    public ApiResponse<UserResponse> banUser(@PathVariable("email") String emailAddress) {
        UserResponse bannedUser = managementServiceImp.banUser(emailAddress);

        return ApiResponseBuilder.buildSuccessResponse("User banned",
                bannedUser);
    }

    @DeleteMapping("/{email}")
    public ApiResponse<UserResponse> deleteUser(@PathVariable("email") String emailAddress) {
        UserResponse deletedUser = managementServiceImp.deleteUser(emailAddress);

        return ApiResponseBuilder.buildSuccessResponse("User deleted",
                deletedUser);
    }
}
