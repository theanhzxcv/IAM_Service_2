package com.theanh.iamservice.IAM_Service_2.Dtos.Request.Authentication;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignInRequest {

    @NotBlank(message = "FIELD_MISSING")
//    @Email(message = "LOGIN_FAILED")
    private String emailAddress;

    @NotBlank(message = "FIELD_MISSING")
//    @Pattern(regexp = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=_]).{8,}$", message = "PASSWORD_POLICY_VIOLATION")
    private String password;
}
