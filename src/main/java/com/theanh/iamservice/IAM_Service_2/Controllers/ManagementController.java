package com.theanh.iamservice.IAM_Service_2.Controllers;

import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Management.UserCreationRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Management.UserSearchRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Management.UserUpdateRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Api.ApiResponse;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Api.PageApiResponse;
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

        return ApiResponse.created(newUser)
                .success("New user created");
    }

    @PreAuthorize("hasPermission('User','Read')")
    @GetMapping
    public PageApiResponse<UserResponse> allUsers(@RequestParam int pageIndex, @RequestParam int pageSize) {
        Page<UserResponse> allUsers = managementServiceImp.allUsers(pageIndex, pageSize);

        return PageApiResponse.of(allUsers.getContent(),
                allUsers.getNumber(),
                allUsers.getSize(),
                allUsers.getTotalElements());
    }

    @PreAuthorize("hasPermission('User','Read')")
    @GetMapping("/search")
    public PageApiResponse<SearchResponse> findUserByKeyword(
            @ParameterObject UserSearchRequest userSearchRequest
    ) {
        Page<SearchResponse> userMatchedFound = managementServiceImp.findUserByKeyWord(userSearchRequest);

        if (userMatchedFound.isEmpty()) {
            return PageApiResponse.failPaging();
        }
        return PageApiResponse.of(userMatchedFound.getContent(),
                userMatchedFound.getNumber(),
                userMatchedFound.getSize(),
                userMatchedFound.getTotalElements());
    }

    @PreAuthorize("hasPermission('User','Update')")
    @PutMapping
    public ApiResponse<UserResponse> updateUser(
            @ParameterObject @Valid UserUpdateRequest userUpdateRequest) {
        UserResponse updatedUser = managementServiceImp.updateUser(userUpdateRequest);

        return ApiResponse.of(updatedUser)
                .success("User with email: " + userUpdateRequest.getEmail() + " updated");
    }

    @PutMapping("/password")
    public ApiResponse<String> changePassword(@RequestParam String email,
                                              @RequestParam String newPassword) {
        String changedPassword = managementServiceImp.changePassword(email, newPassword);

        return ApiResponse.of(changedPassword)
                .success("Password changed");
    }

    @PreAuthorize("hasPermission('User','Delete')")
    @PutMapping("/ban/{email}")
    public ApiResponse<UserResponse> banUser(@PathVariable("email") String emailAddress) {
        UserResponse bannedUser = managementServiceImp.banUser(emailAddress);

        return ApiResponse.of(bannedUser)
                .success("User banned");
    }

    @PreAuthorize("hasPermission('User','Delete')")
    @DeleteMapping("/{email}")
    public ApiResponse<UserResponse> deleteUser(@PathVariable("email") String emailAddress) {
        UserResponse deletedUser = managementServiceImp.deleteUser(emailAddress);

        return ApiResponse.of(deletedUser)
                .success("User deleted");
    }
}
