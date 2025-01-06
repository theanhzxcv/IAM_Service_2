package com.theanh.iamservice.IAM_Service_2.Services;

import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Management.UserCreationRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Management.UserSearchRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Management.UserUpdateRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Management.SearchResponse;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Management.UserResponse;
import org.springframework.data.domain.Page;

public interface IManagementService {

    UserResponse createNewUser(UserCreationRequest userCreationRequest);

    Page<UserResponse> allUsers(int page, int size);

    Page<SearchResponse> findUserByKeyWord(UserSearchRequest userSearchRequest);

//    String resetPassword()

    UserResponse updateUser(String emailAddress, UserUpdateRequest userUpdateRequest);

    UserResponse banUser(String emailAddress);

    UserResponse deleteUser(String emailAddress);
}
