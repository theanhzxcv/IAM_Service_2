package com.theanh.iamservice.IAM_Service_2.Dtos.Response.File;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileImportResponse {
    private String no;
    private String username;
    private String fullName;
    private String dateOfBirth;
    private String street;
    private String ward;
    private String district;
    private String province;
    private String experience;
}
