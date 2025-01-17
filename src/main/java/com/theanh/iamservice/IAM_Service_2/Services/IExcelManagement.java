package com.theanh.iamservice.IAM_Service_2.Services;

import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Management.UserSearchRequest;
import com.theanh.iamservice.IAM_Service_2.Entities.UserEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface IExcelManagement {

    void importUserData(MultipartFile file) throws FileNotFoundException;

    String exportUserData(UserSearchRequest userSearchRequest) throws IOException;
}
