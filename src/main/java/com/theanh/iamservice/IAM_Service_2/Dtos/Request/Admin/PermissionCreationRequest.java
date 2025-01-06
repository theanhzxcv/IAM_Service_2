package com.theanh.iamservice.IAM_Service_2.Dtos.Request.Admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PermissionRequest {
    private String name;
    private String resource;
    private String scope;
}
