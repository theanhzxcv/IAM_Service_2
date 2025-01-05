package com.theanh.iamservice.IAM_Service_2.Dtos.Request.Authentication;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignInRequest {
    private String emailAddress;
    private String password;
}
