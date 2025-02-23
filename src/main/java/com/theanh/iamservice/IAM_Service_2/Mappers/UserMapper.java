package com.theanh.iamservice.IAM_Service_2.Mappers;

import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Authentication.SignUpRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Management.UserCreationRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Admin.RoleResponse;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Management.SearchResponse;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Management.UserResponse;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.User.ProfileResponse;
import com.theanh.iamservice.IAM_Service_2.Entities.UserEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {

    public ProfileResponse toProfileResponse(UserEntity userEntity) {
        if (userEntity == null) {
            throw new IllegalArgumentException("UserEntity cannot be null");
        }

        return ProfileResponse.builder()
                .emailAddress(userEntity.getEmailAddress())
                .username(userEntity.getUsername())
                .firstname(userEntity.getFirstname())
                .lastname(userEntity.getLastname())
                .address(userEntity.getAddress())
                .phoneNumber(userEntity.getPhoneNumber())
                .dateOfBirth(userEntity.getDateOfBirth())
                .build();
    }

    public UserEntity toUserEntity(SignUpRequest signUpRequest) {
        if (signUpRequest == null) {
            throw new IllegalArgumentException("SignUpRequest cannot be null");
        }

        return UserEntity.builder()
                .username(signUpRequest.getUsername())
                .emailAddress(signUpRequest.getEmailAddress())
                .password(signUpRequest.getPassword())
                .firstname(signUpRequest.getFirstname())
                .lastname(signUpRequest.getLastname())
                .build();
    }

    public UserEntity toUserEntity(UserCreationRequest userCreationRequest) {
        if (userCreationRequest == null) {
            throw new IllegalArgumentException("UserCreationRequest cannot be null");
        }

        return UserEntity.builder()
                .username(userCreationRequest.getUsername())
                .emailAddress(userCreationRequest.getEmailAddress())
                .password(userCreationRequest.getPassword())
                .firstname(userCreationRequest.getFirstname())
                .lastname(userCreationRequest.getLastname())
                .address(userCreationRequest.getAddress())
                .phoneNumber(userCreationRequest.getPhoneNumber())
                .dateOfBirth(userCreationRequest.getDateOfBirth())
                .build();
    }

//    public UserEntity toUserEntity(UserUpdateRequest userUpdateRequest) {
//        if (userUpdateRequest == null) {
//            throw new IllegalArgumentException("UserUpdateRequest cannot be null");
//        }
//
//        return UserEntity.builder()
//                .username(userUpdateRequest.getUsername())
//                .firstname(userUpdateRequest.getFirstname())
//                .lastname(userUpdateRequest.getLastname())
//                .address(userUpdateRequest.getAddress())
//                .phoneNumber(userUpdateRequest.getPhoneNumber())
//                .dateOfBirth(userUpdateRequest.getDateOfBirth())
//                .build();
//    }

    public UserResponse toUserResponse(UserEntity userEntity) {
        if (userEntity == null) {
            throw new IllegalArgumentException("UserEntity cannot be null");
        }

        return UserResponse.builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .emailAddress(userEntity.getEmailAddress())
                .password(userEntity.getPassword())
                .firstname(userEntity.getFirstname())
                .lastname(userEntity.getLastname())
                .address(userEntity.getAddress())
                .phoneNumber(userEntity.getPhoneNumber())
                .dateOfBirth(userEntity.getDateOfBirth())
                .isDeleted(userEntity.isDeleted())
                .isBanned(userEntity.isBanned())
                .createdBy(userEntity.getCreatedBy())
                .createdAt(userEntity.getCreatedAt())
                .lastModifiedBy(userEntity.getLastModifiedBy())
                .lastModifiedAt(userEntity.getLastModifiedAt())
                .build();
    }

    public UserResponse toUserResponse(UserEntity userEntity, List<RoleResponse> roleResponses) {
        if (userEntity == null) {
            throw new IllegalArgumentException("UserEntity cannot be null");
        }

        return UserResponse.builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .emailAddress(userEntity.getEmailAddress())
                .password(userEntity.getPassword())
                .firstname(userEntity.getFirstname())
                .lastname(userEntity.getLastname())
                .address(userEntity.getAddress())
                .phoneNumber(userEntity.getPhoneNumber())
                .dateOfBirth(userEntity.getDateOfBirth())
                .isDeleted(userEntity.isDeleted())
                .isBanned(userEntity.isBanned())
                .createdBy(userEntity.getCreatedBy())
                .createdAt(userEntity.getCreatedAt())
                .lastModifiedBy(userEntity.getLastModifiedBy())
                .lastModifiedAt(userEntity.getLastModifiedAt())
                .roleResponses(roleResponses)
                .build();
    }

    public SearchResponse toSearchResponse(UserEntity userEntity) {
        if (userEntity == null) {
            throw new IllegalArgumentException("UserEntity cannot be null");
        }

        return SearchResponse.builder()
                .username(userEntity.getUsername())
                .emailAddress(userEntity.getEmailAddress())
                .password(userEntity.getPassword())
                .firstname(userEntity.getFirstname())
                .lastname(userEntity.getLastname())
                .address(userEntity.getAddress())
                .phoneNumber(userEntity.getPhoneNumber())
                .dateOfBirth(userEntity.getDateOfBirth())
                .build();
    }
}
