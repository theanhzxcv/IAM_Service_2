package com.theanh.iamservice.IAM_Service_2.Dtos.Request.Management;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchRequest{
    private String keyword;
    private int pageIndex;
    private int pageSize;
}
