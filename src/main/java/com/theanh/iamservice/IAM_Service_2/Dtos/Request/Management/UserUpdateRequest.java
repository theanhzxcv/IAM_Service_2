package com.theanh.iamservice.IAM_Service_2.Dtos.Request.Management;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {

    @NotBlank(message = "FIELD_MISSING")
    private String email;

    @NotBlank(message = "FIELD_MISSING")
    private String username;

    @NotBlank(message = "FIELD_MISSING")
    private String firstname;

    @NotBlank(message = "FIELD_MISSING")
    private String lastname;

    @NotBlank(message = "FIELD_MISSING")
    private String isDeleted;

    @NotBlank(message = "FIELD_MISSING")
    private String isBanned;

    private List<String> roles;
}
