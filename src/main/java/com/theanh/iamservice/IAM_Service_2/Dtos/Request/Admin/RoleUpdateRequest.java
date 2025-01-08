package com.theanh.iamservice.IAM_Service_2.Dtos.Request.Admin;

import jakarta.validation.constraints.NotBlank;
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
    @NotBlank(message = "FIELD_MISSING")
    private List<String> permissions;

    @NotBlank(message = "FIELD_MISSING")
    private String isRoot;

    @NotBlank(message = "FIELD_MISSING")
    private String isDeleted;
}
