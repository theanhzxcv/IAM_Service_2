package com.theanh.iamservice.IAM_Service_2.Services;

import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Admin.PermissionRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Admin.PermissionResponse;
import org.springframework.data.domain.Page;

public interface IPermissionService {

    PermissionResponse createPermission(PermissionRequest permissionRequest);

    PermissionResponse updatePermission(Long id, PermissionRequest permissionRequest);

    Page<PermissionResponse> allPermissions(int page, int size);

    String deletePermission(Long id);
}
