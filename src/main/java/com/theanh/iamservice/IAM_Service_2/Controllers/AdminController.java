package com.theanh.iamservice.IAM_Service_2.Controllers;

import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Admin.PermissionRequest;
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
            @ParameterObject PermissionRequest permissionRequest) {
        PermissionResponse newPermission = permissionServiceImp.createPermission(permissionRequest);

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

    @PatchMapping("/{id}")
    public ApiResponse<PermissionResponse> updatePermission(
            @PathVariable("id") Long id,
            @ParameterObject PermissionRequest permissionRequest) {
        PermissionResponse updatedPermission = permissionServiceImp.updatePermission(id, permissionRequest);

        return ApiResponseBuilder.buildSuccessResponse("Updated permissions",
                updatedPermission);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deletePermission(@PathVariable Long id) {
        String deletedPermission = permissionServiceImp.deletePermission(id);

        return ApiResponseBuilder.buildSuccessResponse("Permission deleted",
                deletedPermission);
    }
}
