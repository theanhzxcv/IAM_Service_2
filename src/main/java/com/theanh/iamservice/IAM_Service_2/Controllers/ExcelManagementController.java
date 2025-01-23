package com.theanh.iamservice.IAM_Service_2.Controllers;

import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Management.UserSearchRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Api.ApiResponse;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.File.FileImportResponse;
import com.theanh.iamservice.IAM_Service_2.Services.ServiceImp.ExcelManagementImp.ExcelManagementService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files/excel")
@Tag(name = "Excel")
public class ExcelManagementController {
    private final ExcelManagementService excelManagementService;

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<List<FileImportResponse>>importUserData (@RequestParam("file") MultipartFile file) {
        List<FileImportResponse> fileImportResponse = excelManagementService.importUserData(file);
        return ApiResponse.of(fileImportResponse).success("Import successfully.");
    }

    @PostMapping(value = "/export")
    public ApiResponse<String> exportUserData (@RequestParam(value = "keyword") String keyword)
            throws IOException {
        String exportedToExcelFile = excelManagementService.exportUserData(keyword);
        return ApiResponse.of(exportedToExcelFile).success("Export successfully.");
    }
}
