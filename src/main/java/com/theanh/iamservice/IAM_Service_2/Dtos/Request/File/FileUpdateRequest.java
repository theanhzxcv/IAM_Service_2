package com.theanh.iamservice.IAM_Service_2.Dtos.Request.File;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileUpdateRequest {
    private String description;
    private String owner;
    private String isPublic;
    private String isDeleted;
}
