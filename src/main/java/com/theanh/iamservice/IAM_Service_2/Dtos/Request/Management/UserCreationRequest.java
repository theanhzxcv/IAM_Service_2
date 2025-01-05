package com.theanh.iamservice.IAM_Service_2.Dtos.Request.Management;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UserCreationRequest {
    private String username;
    private String emailAddress;
    private String password;

    private String firstname;
    private String lastname;
    private String address;
    private String phoneNumber;
    private LocalDate dateOfBirth;
}
