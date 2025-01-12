package com.theanh.iamservice.IAM_Service_2.Controllers;

import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Api.ApiResponse;
import com.theanh.iamservice.IAM_Service_2.Services.ServiceImp.StorageImp.StorageManagementServiceImp;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/storage")
public class CallStorageController {
    private final StorageManagementServiceImp storageManagementServiceImp;

    @PostMapping("/upload")
    public ApiResponse<String> callStorageUpload(HttpServletRequest request) {

        String result = storageManagementServiceImp.uploadFileToStorageService(request);
        return ApiResponse.of(result).success("Call successfully");
    }
}
