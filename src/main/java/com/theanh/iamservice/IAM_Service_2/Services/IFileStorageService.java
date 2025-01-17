package com.theanh.iamservice.IAM_Service_2.Services;

import com.theanh.iamservice.IAM_Service_2.Dtos.Request.File.FileSearchRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.File.FileUpdateRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Api.ApiResponse;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Api.PageApiResponse;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.File.FileResponse;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IFileStorageService {
    String greet();

    ApiResponse<FileResponse> uploadFile(MultipartFile file, String description);

    ApiResponse<List<FileResponse>> uploadMultipleFiles(List<MultipartFile> files, String description);

    ApiResponse<String> uploadProfileImage(MultipartFile file);

    ApiResponse<FileResponse> getFileByName(String fileName);

    PageApiResponse<FileResponse> searchFile(FileSearchRequest fileSearchRequest);

    ApiResponse<FileResponse> updateFile(String fileName, FileUpdateRequest fileUpdateRequest);

    void deletedFile(String fileName);

    ResponseEntity<byte[]> viewImage(String imageFile, Double ratio, Integer height, Integer width);

}