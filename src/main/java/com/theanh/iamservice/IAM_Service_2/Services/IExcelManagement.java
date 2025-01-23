package com.theanh.iamservice.IAM_Service_2.Services;

import com.theanh.iamservice.IAM_Service_2.Dtos.Response.File.FileImportResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface IExcelManagement {

    List<FileImportResponse> importUserData(MultipartFile file) throws FileNotFoundException;

    String exportUserData(String keyword) throws IOException;
}
