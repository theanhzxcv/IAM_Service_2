package com.theanh.iamservice.IAM_Service_2.Controllers;

import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Admin.PermissionCreationRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Admin.PermissionUpdateRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Admin.RoleCreationRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Admin.RoleUpdateRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Admin.PermissionResponse;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Admin.RoleResponse;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Api.ApiResponse;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Api.ApiResponseBuilder;
import com.theanh.iamservice.IAM_Service_2.Entities.RoleEntity;
import com.theanh.iamservice.IAM_Service_2.Services.ServiceImp.Admin.PermissionServiceImp;
import com.theanh.iamservice.IAM_Service_2.Services.ServiceImp.Admin.RoleServiceImp;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@Tag(name = "Admin")
public class AdminController {
    private final RoleServiceImp roleServiceImp;
    private final PermissionServiceImp permissionServiceImp;

    @PostMapping("/permissions")
    public ApiResponse<PermissionResponse> createPermission(
            @ParameterObject PermissionCreationRequest permissionCreationRequest) {
        PermissionResponse newPermission = permissionServiceImp.createPermission(permissionCreationRequest);

        return ApiResponseBuilder.createdSuccessResponse("New permission created",
                newPermission);
    }

    @GetMapping("/permissions")
    public ApiResponse<Page<PermissionResponse>> allPermission(@RequestParam int page,
                                                               @RequestParam int size) {
        Page<PermissionResponse> allPermission = permissionServiceImp.allPermissions(page, size);

        return ApiResponseBuilder.buildSuccessResponse("All permissions",
                allPermission);
    }

    @PatchMapping("/permissions/{name}")
    public ApiResponse<PermissionResponse> updatePermission(
            @PathVariable("name") String name,
            @ParameterObject PermissionUpdateRequest permissionUpdateRequest) {
        PermissionResponse updatedPermission = permissionServiceImp.updatePermission(name, permissionUpdateRequest);

        return ApiResponseBuilder.buildSuccessResponse("Updated permissions",
                updatedPermission);
    }

    @DeleteMapping("/permissions/{name}")
    public ApiResponse<String> deletePermission(@PathVariable("name") String name) {
        String deletedPermission = permissionServiceImp.deletePermission(name);

        return ApiResponseBuilder.buildSuccessResponse("Permission deleted",
                deletedPermission);
    }

    @PostMapping("/roles")
    public ApiResponse<RoleResponse> createRole(
            @ParameterObject RoleCreationRequest roleCreationRequest) {
        RoleResponse newRole = roleServiceImp.createRole(roleCreationRequest);

        return ApiResponseBuilder.createdSuccessResponse("New role created",
                newRole);
    }

    @GetMapping("/roles")
    public ApiResponse<Page<RoleResponse>> allRoles(@RequestParam int page,
                                                    @RequestParam int size) {
        Page<RoleResponse> allRoles = roleServiceImp.allRoles(page, size);

        return ApiResponseBuilder.buildSuccessResponse("All roles",
                allRoles);
    }

    @PatchMapping("/roles/{name}")
    public ApiResponse<RoleResponse> updateRole(@PathVariable("name") String name,
                                                @ParameterObject RoleUpdateRequest roleUpdateRequest) {
        RoleResponse updatedRole = roleServiceImp.updateRole(name, roleUpdateRequest);

        return ApiResponseBuilder.buildSuccessResponse("Role updated",
                updatedRole);
    }

    @DeleteMapping("/roles/{name}")
    public ApiResponse<String> deleteRole(@PathVariable("name") String name) {
        String deletedRole = roleServiceImp.deleteRole(name);

        return ApiResponseBuilder.buildSuccessResponse("Role deleted",
                deletedRole);
    }
}
