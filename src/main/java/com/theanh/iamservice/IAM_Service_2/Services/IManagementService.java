package com.theanh.iamservice.IAM_Service_2.Services;

import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Management.UserCreationRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Management.UserSearchRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Management.UserUpdateRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Management.SearchResponse;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Management.UserResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IManagementService {

    UserResponse createNewUser(UserCreationRequest userCreationRequest);

    Page<UserResponse> allUsers(int pageIndex, int pageSize);

    Page<SearchResponse> findUserByKeyWord(UserSearchRequest userSearchRequest);

    String changePassword(String emailAddress, String newPassword);

//    String resetPassword()

    UserResponse updateUser(UserUpdateRequest userUpdateRequest);

    UserResponse banUser(String emailAddress);

    UserResponse deleteUser(String emailAddress);
}
