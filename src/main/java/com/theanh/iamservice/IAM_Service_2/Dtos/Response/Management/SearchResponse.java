package com.theanh.iamservice.IAM_Service_2.Dtos.Response.Management;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResponse {
    private String username;
    private String emailAddress;
    private String password;

    private String firstname;
    private String lastname;
    private String address;
    private String phoneNumber;
    private LocalDate dateOfBirth;
}
