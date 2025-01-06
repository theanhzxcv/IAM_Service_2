package com.theanh.iamservice.IAM_Service_2.Dtos.Response.Admin;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PermissionResponse {
    private String name;
    private String resource;
    private String scope;
    private boolean isDeleted;
}
