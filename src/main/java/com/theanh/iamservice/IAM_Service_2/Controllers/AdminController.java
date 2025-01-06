package com.theanh.iamservice.IAM_Service_2.Controllers;

import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Admin.PermissionCreationRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Admin.PermissionUpdateRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Admin.PermissionResponse;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Api.ApiResponse;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Api.ApiResponseBuilder;
import com.theanh.iamservice.IAM_Service_2.Services.ServiceImp.Admin.PermissionServiceImp;
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
    private final PermissionServiceImp permissionServiceImp;

    @PostMapping
    public ApiResponse<PermissionResponse> createPermission(
            @ParameterObject PermissionCreationRequest permissionCreationRequest) {
        PermissionResponse newPermission = permissionServiceImp.createPermission(permissionCreationRequest);

        return ApiResponseBuilder.createdSuccessResponse("New permission created",
                newPermission);
    }

    @GetMapping
    public ApiResponse<Page<PermissionResponse>> allPermission(@RequestParam int page,
                                                               @RequestParam int size) {
        Page<PermissionResponse> allPermission = permissionServiceImp.allPermissions(page, size);

        return ApiResponseBuilder.buildSuccessResponse("All permissions",
                allPermission);
    }

    @PatchMapping("/{name}")
    public ApiResponse<PermissionResponse> updatePermission(
            @PathVariable("name") String name,
            @ParameterObject PermissionUpdateRequest permissionUpdateRequest) {
        PermissionResponse updatedPermission = permissionServiceImp.updatePermission(name, permissionUpdateRequest);

        return ApiResponseBuilder.buildSuccessResponse("Updated permissions",
                updatedPermission);
    }

    @DeleteMapping("/{name}")
    public ApiResponse<String> deletePermission(@PathVariable("name") String name) {
        String deletedPermission = permissionServiceImp.deletePermission(name);

        return ApiResponseBuilder.buildSuccessResponse("Permission deleted",
                deletedPermission);
    }
}
