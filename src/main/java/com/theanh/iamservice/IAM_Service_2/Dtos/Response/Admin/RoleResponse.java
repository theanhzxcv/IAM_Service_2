package com.theanh.iamservice.IAM_Service_2.Dtos.Response.Admin;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponse {
    private String name;
    private boolean isDeleted;
    List<PermissionResponse> permissions;
}
