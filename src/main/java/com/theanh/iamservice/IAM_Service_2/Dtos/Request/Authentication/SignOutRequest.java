package com.theanh.iamservice.IAM_Service_2.Dtos.Request.Authentication;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignOutRequest {
    private String refreshToken;
}
