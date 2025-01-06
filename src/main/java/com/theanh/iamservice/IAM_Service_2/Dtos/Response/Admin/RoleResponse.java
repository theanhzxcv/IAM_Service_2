package com.theanh.iamservice.IAM_Service_2.Dtos.Response.Admin;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class RoleResponse {
    private Long id;
    private String name;
}
