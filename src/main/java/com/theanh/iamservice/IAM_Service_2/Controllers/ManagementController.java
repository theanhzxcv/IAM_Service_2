package com.theanh.iamservice.IAM_Service_2.Controllers;

import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Management.UserCreationRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Management.UserSearchRequest;
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

    @GetMapping("/{id}")
    public ApiResponse<UserResponse> findUserById(@PathVariable("id") UUID id) {
        UserResponse result = managementServiceImp.findUserById(id);

        return ApiResponseBuilder.buildSuccessResponse("User with id: " + id + " found",
                result);
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

    @PutMapping("/ban/{id}")
    public ApiResponse<UserResponse> banUser(@PathVariable("id") UUID id) {
        UserResponse bannedUser = managementServiceImp.banUser(id);

        return ApiResponseBuilder.buildSuccessResponse("User banned",
                bannedUser);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<UserResponse> deleteUser(@PathVariable("id") UUID id) {
        UserResponse deletedUser = managementServiceImp.deleteUser(id);

        return ApiResponseBuilder.buildSuccessResponse("User deleted",
                deletedUser);
    }
}
