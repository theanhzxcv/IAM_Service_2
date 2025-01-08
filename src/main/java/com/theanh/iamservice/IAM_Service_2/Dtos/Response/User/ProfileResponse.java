package com.theanh.iamservice.IAM_Service_2.Dtos.Response.User;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponse {
    private String emailAddress;
    private String username;
    private String firstname;
    private String lastname;
    private String address;
    private String phoneNumber;
    private LocalDate dateOfBirth;
}
