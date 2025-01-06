package com.theanh.iamservice.IAM_Service_2.Services;

import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Admin.PermissionCreationRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Admin.PermissionUpdateRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Admin.PermissionResponse;
import org.springframework.data.domain.Page;

public interface IPermissionService {

    PermissionResponse createPermission(PermissionCreationRequest permissionCreationRequest);

    PermissionResponse updatePermission(String name, PermissionUpdateRequest permissionUpdateRequest);

    Page<PermissionResponse> allPermissions(int page, int size);

    String deletePermission(String name);
}
