package com.theanh.iamservice.IAM_Service_2.Dtos.Request.User;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordChangeRequest {

    @NotBlank(message = "FIELD_MISSING")
    private String oldPassword;

    @NotBlank(message = "FIELD_MISSING")
    private String newPassword;

    @NotBlank(message = "FIELD_MISSING")
    private String confirmationPassword;
}
