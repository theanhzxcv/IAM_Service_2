package com.theanh.iamservice.IAM_Service_2.Dtos.Request.Admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleUpdateRequest {
    private List<String> permissions;
    private boolean isDeleted;
}
