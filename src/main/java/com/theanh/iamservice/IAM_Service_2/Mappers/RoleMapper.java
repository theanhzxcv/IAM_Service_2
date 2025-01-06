package com.theanh.iamservice.IAM_Service_2.Mappers;

import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Admin.RoleCreationRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Admin.PermissionResponse;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Admin.RoleResponse;
import com.theanh.iamservice.IAM_Service_2.Entities.PermissionEntity;
import com.theanh.iamservice.IAM_Service_2.Entities.RoleEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RoleMapper {

    public RoleEntity tpRoleEntity(RoleCreationRequest roleCreationRequest) {
        if (roleCreationRequest == null) {
            throw new IllegalArgumentException("RoleCreationRequest cannot br null");
        }

        return RoleEntity.builder()
                .name(roleCreationRequest.getName())
                .isDeleted(false)
                .build();
    }

    public RoleResponse toRoleResponse(RoleEntity roleEntity) {
        if (roleEntity == null) {
            throw new IllegalArgumentException("RoleCreationRequest cannot br null");
        }

        return RoleResponse.builder()
                .name(roleEntity.getName())
                .isDeleted(roleEntity.isDeleted())
                .build();
    }

    public RoleResponse toRoleResponse(RoleEntity roleEntity, List<PermissionResponse> permissionResponses) {
        if (roleEntity == null) {
            throw new IllegalArgumentException("RoleCreationRequest cannot br null");
        }

        return RoleResponse.builder()
                .name(roleEntity.getName())
                .isDeleted(roleEntity.isDeleted())
                .permissions(permissionResponses)
                .build();
    }

    public RoleResponse toRoleResponseWithPermissions(RoleEntity role, List<PermissionResponse> permissions) {
        List<PermissionResponse> permissionResponses = permissions.stream()
                .map(permission -> new PermissionResponse(
                        permission.getName(),
                        permission.getResource(),
                        permission.getScope(),
                        permission.isDeleted()))
                .collect(Collectors.toList());

        return RoleResponse.builder()
                .name(role.getName())
                .isDeleted(role.isDeleted())
                .permissions(permissionResponses)
                .build();
    }

    public RoleResponse toRoleResponseWithPermission(RoleEntity role, List<PermissionEntity> permissions) {
        List<PermissionResponse> permissionResponses = permissions.stream()
                .map(permission -> new PermissionResponse(
                        permission.getName(),
                        permission.getResource(),
                        permission.getScope(),
                        permission.isDeleted()))
                .collect(Collectors.toList());

        return RoleResponse.builder()
                .name(role.getName())
                .isDeleted(role.isDeleted())
                .permissions(permissionResponses)
                .build();
    }
}
