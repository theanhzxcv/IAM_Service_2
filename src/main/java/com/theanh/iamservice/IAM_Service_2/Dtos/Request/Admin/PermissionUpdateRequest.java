package com.theanh.iamservice.IAM_Service_2.Dtos.Request.Admin;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PermissionUpdateRequest {
    @NotBlank(message = "FIELD_MISSING")
    private String resource;

    @NotBlank(message = "FIELD_MISSING")
    private String scope;

    @NotBlank(message = "FIELD_MISSING")
    private String isDeleted;
}
