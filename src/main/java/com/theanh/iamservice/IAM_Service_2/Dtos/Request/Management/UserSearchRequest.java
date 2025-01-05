package com.theanh.iamservice.IAM_Service_2.Dtos.Request.Management;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSearchRequest {
    private String keyword;
    private int pageIndex = 1;
    private int pageSize = 10;
}
