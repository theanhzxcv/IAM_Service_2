package com.theanh.iamservice.IAM_Service_2.Services.ServiceImp.FileStorageImp;

import com.theanh.iamservice.IAM_Service_2.Dtos.Request.File.FileSearchRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.File.FileUpdateRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Api.ApiResponse;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Api.PageApiResponse;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.File.FileResponse;
import com.theanh.iamservice.IAM_Service_2.Entities.UserEntity;
import com.theanh.iamservice.IAM_Service_2.Repositories.FeignClients.PrivateFileClient;
import com.theanh.iamservice.IAM_Service_2.Repositories.UserRepository;
import com.theanh.iamservice.IAM_Service_2.Services.IFileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PrivateFileStorageServiceImp implements IFileStorageService {

    private final PrivateFileClient privateFileClient;
    private final UserRepository userRepository;

    private String getCurrentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @Override
    public String greet() {
        return privateFileClient.greet();
    }

    @Override
    public ApiResponse<FileResponse> uploadFile(MultipartFile file, String description) {
        return privateFileClient.uploadFile(file, description);
    }

    @Override
    public ApiResponse<List<FileResponse>> uploadMultipleFiles(List<MultipartFile> files, String description) {
        return privateFileClient.uploadMultipleFiles(files, description);
    }

    @Override
    public ApiResponse<String> uploadProfileImage(MultipartFile image) {
        String avatarId = privateFileClient.uploadProfileImage(image).getData();
        String email = getCurrentUserEmail();
        UserEntity userEntity = userRepository.findByEmailAddress(email)
                        .orElseThrow(() -> new RuntimeException("User not found"));
        userEntity.setAvatarID(avatarId);
        userRepository.save(userEntity);

        return ApiResponse.ok();
    }

    @Override
    public ApiResponse<FileResponse> getFileByName(String fileName) {
        return privateFileClient.getFileByName(fileName);
    }

    @Override
    public PageApiResponse<FileResponse> searchFile(FileSearchRequest fileSearchRequest) {
        return privateFileClient.searchFile(fileSearchRequest);
    }

    @Override
    public ApiResponse<FileResponse> updateFile(String fileName, FileUpdateRequest fileUpdateRequest) {
        return privateFileClient.updateFile(fileName, fileUpdateRequest);
    }

    @Override
    public void deletedFile(String fileName) {
        privateFileClient.deleteFile(fileName);
    }

    @Override
    public ResponseEntity<byte[]> viewImage(String imageFile, Double ratio, Integer height, Integer width) {
        return privateFileClient.viewImage(imageFile, ratio, height, width);
    }

}
