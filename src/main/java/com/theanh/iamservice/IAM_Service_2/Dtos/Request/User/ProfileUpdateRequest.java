package com.theanh.iamservice.IAM_Service_2.Dtos.Request.User;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateRequest {

    @NotBlank(message = "FIELD_MISSING")
    private String username;

    @NotBlank(message = "FIELD_MISSING")
    private String firstname;

    @NotBlank(message = "FIELD_MISSING")
    private String lastname;

    @NotBlank(message = "FIELD_MISSING")
    private String address;

    @NotBlank(message = "FIELD_MISSING")
    private String phoneNumber;

    private LocalDate dateOfBirth;
}
