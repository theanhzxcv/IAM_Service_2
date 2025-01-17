package com.theanh.iamservice.IAM_Service_2.Services.ServiceImp.FileStorageImp;

import com.theanh.iamservice.IAM_Service_2.Dtos.Request.File.FileSearchRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.File.FileUpdateRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Api.ApiResponse;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Api.PageApiResponse;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.File.FileResponse;
import com.theanh.iamservice.IAM_Service_2.Entities.UserEntity;
import com.theanh.iamservice.IAM_Service_2.Repositories.FeignClients.PrivateFileClient;
import com.theanh.iamservice.IAM_Service_2.Repositories.FeignClients.PublicFeignClient;
import com.theanh.iamservice.IAM_Service_2.Repositories.UserRepository;
import com.theanh.iamservice.IAM_Service_2.Services.IFileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PublicFileStorageServiceImp implements IFileStorageService {

    private final PublicFeignClient publicFeignClient;
    private final UserRepository userRepository;

    private String getCurrentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @Override
    public String greet() {
        return "";
    }

    @Override
    public ApiResponse<FileResponse> uploadFile(MultipartFile file, String description) {
        return publicFeignClient.uploadFile(file, description);
    }

    @Override
    public ApiResponse<List<FileResponse>> uploadMultipleFiles(List<MultipartFile> files, String description) {
        return publicFeignClient.uploadMultipleFiles(files, description);
    }

    @Override
    public ApiResponse<String> uploadProfileImage(MultipartFile image) {
        return null;
    }

    @Override
    public ApiResponse<FileResponse> getFileByName(String fileName) {
        return publicFeignClient.getFileByName(fileName);
    }

    @Override
    public PageApiResponse<FileResponse> searchFile(FileSearchRequest fileSearchRequest) {
        return publicFeignClient.searchFile(fileSearchRequest);
    }

    @Override
    public ApiResponse<FileResponse> updateFile(String fileName, FileUpdateRequest fileUpdateRequest) {
        return publicFeignClient.updateFile(fileName, fileUpdateRequest);
    }

    @Override
    public void deletedFile(String fileName) {
        publicFeignClient.deleteFile(fileName);
    }

    @Override
    public ResponseEntity<byte[]> viewImage(String imageFile, Double ratio, Integer height, Integer width) {
        return null;
    }
}
