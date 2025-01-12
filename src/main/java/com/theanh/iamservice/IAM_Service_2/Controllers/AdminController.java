package com.theanh.iamservice.IAM_Service_2.Controllers;

import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Admin.PermissionCreationRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Admin.PermissionUpdateRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Admin.RoleCreationRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Admin.RoleUpdateRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Admin.PermissionResponse;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Admin.RoleResponse;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Api.ApiResponse;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Api.PageApiResponse;
import com.theanh.iamservice.IAM_Service_2.Services.ServiceImp.AdminImp.PermissionServiceImp;
import com.theanh.iamservice.IAM_Service_2.Services.ServiceImp.AdminImp.RoleServiceImp;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@Tag(name = "Admin")
public class AdminController {
    private final RoleServiceImp roleServiceImp;
    private final PermissionServiceImp permissionServiceImp;

    @PreAuthorize("hasPermission('Permission','Create')")
    @PostMapping("/permissions")
    public ApiResponse<PermissionResponse> createPermission(
            @ParameterObject @Valid PermissionCreationRequest permissionCreationRequest) {
        PermissionResponse newPermission = permissionServiceImp.createPermission(permissionCreationRequest);

        return ApiResponse.of(newPermission)
                .success("New permission created.");
    }

    @PreAuthorize("hasPermission('Permission','Read')")
    @GetMapping("/permissions")
    public PageApiResponse<PermissionResponse> allPermission(@RequestParam int pageIndex,
                                                               @RequestParam int pageSize) {
        Page<PermissionResponse> allPermission = permissionServiceImp.allPermissions(pageIndex, pageSize);

        return PageApiResponse.of(allPermission.getContent(),
                allPermission.getNumber(),
                allPermission.getSize(),
                allPermission.getTotalElements());
    }

    @PreAuthorize("hasPermission('Permission','Update')")
    @PatchMapping("/permissions/{name}")
    public ApiResponse<PermissionResponse> updatePermission(
            @PathVariable("name") String name,
            @ParameterObject @Valid PermissionUpdateRequest permissionUpdateRequest) {
        PermissionResponse updatedPermission = permissionServiceImp.updatePermission(name, permissionUpdateRequest);

        return ApiResponse.of(updatedPermission)
                .success("Permission updated.");
    }

    @PreAuthorize("hasPermission('Permission','Delete')")
    @DeleteMapping("/permissions/{name}")
    public ApiResponse<String> deletePermission(@PathVariable("name") String name) {
        String deletedPermission = permissionServiceImp.deletePermission(name);

        return ApiResponse.of(deletedPermission)
                .success("Permission deleted.");
    }

    @PreAuthorize("hasPermission('Role','Create')")
    @PostMapping("/roles")
    public ApiResponse<RoleResponse> createRole(
            @ParameterObject @Valid RoleCreationRequest roleCreationRequest) {
        RoleResponse newRole = roleServiceImp.createRole(roleCreationRequest);

        return ApiResponse.of(newRole)
                .success("New role created");
    }

    @PreAuthorize("hasPermission('Role','Read')")
    @GetMapping("/roles")
    public PageApiResponse<RoleResponse> allRoles(@RequestParam int pageIndex,
                                                    @RequestParam int pageSize) {
        Page<RoleResponse> allRoles = roleServiceImp.allRoles(pageIndex, pageSize);

        return PageApiResponse.of(allRoles.getContent(),
                        allRoles.getNumber(),
                        allRoles.getSize(),
                        allRoles.getTotalElements());
    }

    @PreAuthorize("hasPermission('Role','Update')")
    @PatchMapping("/roles/{name}")
    public ApiResponse<RoleResponse> updateRole(@PathVariable("name") String name,
                                                @ParameterObject @Valid RoleUpdateRequest roleUpdateRequest) {
        RoleResponse updatedRole = roleServiceImp.updateRole(name, roleUpdateRequest);

        return ApiResponse.of(updatedRole)
                .success("Role updated");
    }

    @PreAuthorize("hasPermission('Role','Delete')")
    @DeleteMapping("/roles/{name}")
    public ApiResponse<String> deleteRole(@PathVariable("name") String name) {
        String deletedRole = roleServiceImp.deleteRole(name);

        return ApiResponse.of(deletedRole).success("Role deleted");
    }
}
