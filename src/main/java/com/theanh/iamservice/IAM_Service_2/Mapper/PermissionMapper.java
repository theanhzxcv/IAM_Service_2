package com.theanh.iamservice.IAM_Service_2.Mapper;

import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Admin.PermissionCreationRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Admin.PermissionResponse;
import com.theanh.iamservice.IAM_Service_2.Entities.PermissionEntity;
import org.springframework.stereotype.Component;

@Component
public class PermissionMapper {

    public PermissionEntity toPermissionEntity(PermissionCreationRequest permissionCreationRequest) {
        if (permissionCreationRequest == null) {
            throw new IllegalArgumentException("SignUpRequest cannot be null");
        }

        return PermissionEntity
                .builder()
                .name(permissionCreationRequest.getName())
                .resource(permissionCreationRequest.getResource())
                .scope(permissionCreationRequest.getScope())
                .build();
    }

    public PermissionResponse toPermissionResponse(PermissionEntity permissionEntity) {
        if (permissionEntity == null) {
            throw new IllegalArgumentException("SignUpRequest cannot be null");
        }

        return PermissionResponse.builder()
                .name(permissionEntity.getName())
                .resource(permissionEntity.getResource())
                .scope(permissionEntity.getScope())
                .isDeleted(permissionEntity.isDeleted())
                .build();
    }
}
