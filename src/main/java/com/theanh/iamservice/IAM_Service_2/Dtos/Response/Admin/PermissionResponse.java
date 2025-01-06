package com.theanh.iamservice.IAM_Service_2.Dtos.Response.Admin;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionResponse {
    private String name;
    private String resource;
    private String scope;
    private boolean isDeleted;
}
