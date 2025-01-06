package com.theanh.iamservice.IAM_Service_2.Mapper;

import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Admin.PermissionRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Admin.PermissionResponse;
import com.theanh.iamservice.IAM_Service_2.Entities.PermissionEntity;
import org.springframework.stereotype.Component;

@Component
public class PermissionMapper {

    public PermissionEntity toPermissionEntity(PermissionRequest permissionRequest) {
        if (permissionRequest == null) {
            throw new IllegalArgumentException("SignUpRequest cannot be null");
        }

        return PermissionEntity
                .builder()
                .resource(permissionRequest.getResource())
                .scope(permissionRequest.getScope())
                .build();
    }

    public PermissionResponse toPermissionResponse(PermissionEntity permissionEntity) {
        if (permissionEntity == null) {
            throw new IllegalArgumentException("SignUpRequest cannot be null");
        }

        return PermissionResponse.builder()
                .id(permissionEntity.getId())
                .resource(permissionEntity.getResource())
                .scope(permissionEntity.getScope())
                .build();
    }
}
