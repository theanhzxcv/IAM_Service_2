package com.theanh.iamservice.IAM_Service_2.Services;

import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Admin.RoleCreationRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Admin.RoleUpdateRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Admin.RoleResponse;
import org.springframework.data.domain.Page;

public interface IRoleService {

    RoleResponse createRole(RoleCreationRequest roleCreationRequest);

    RoleResponse updateRole(String name, RoleUpdateRequest roleUpdateRequest);

    Page<RoleResponse> allRoles(int page, int size);

    String deleteRole(String name);
}
