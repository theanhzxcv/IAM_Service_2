package com.theanh.iamservice.IAM_Service_2.Dtos.Request.Management;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCreationRequest {
    @NotBlank(message = "FIELD_MISSING")
    private String username;

    @NotBlank(message = "FIELD_MISSING")
    private String emailAddress;

    @NotBlank(message = "FIELD_MISSING")
    private String password;

    @NotBlank(message = "FIELD_MISSING")
    private String firstname;

    @NotBlank(message = "FIELD_MISSING")
    private String lastname;

    @NotBlank(message = "FIELD_MISSING")
    private String address;

    @NotBlank(message = "FIELD_MISSING")
    private String phoneNumber;

    private LocalDate dateOfBirth;

    private List<String> roles;
}
