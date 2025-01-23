package com.theanh.iamservice.IAM_Service_2.Controllers;

import com.theanh.iamservice.IAM_Service_2.Dtos.Request.File.FileSearchRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.File.FileUpdateRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Api.ApiResponse;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Api.PageApiResponse;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.File.FileResponse;
import com.theanh.iamservice.IAM_Service_2.Services.ServiceImp.FileStorageImp.PrivateFileStorageServiceImp;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files/private")
@Tag(name = "PrivateFileStorage")
public class PrivateFileController {
    private static final Logger logger = LoggerFactory.getLogger(PrivateFileController.class);
    private final PrivateFileStorageServiceImp privateFileStorageServiceImp;

    @GetMapping("/greet")
    public ApiResponse<String> greet() {
        return ApiResponse.of(privateFileStorageServiceImp.greet()).success("Pass");
    }

    @PostMapping(path = "/profile/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> uploadProfileImage(@RequestParam("image") MultipartFile image) {

        return privateFileStorageServiceImp.uploadProfileImage(image);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<FileResponse> uploadFile(@RequestParam("file") MultipartFile file,
                                                             @RequestParam("description") String description) {

        return privateFileStorageServiceImp.uploadFile(file, description);
    }

    @PostMapping(path = "/multiple-files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<List<FileResponse>> uploadMultipleFile(
            @RequestParam("file") List<MultipartFile> files,
            @RequestParam("description") String description) {

        return privateFileStorageServiceImp.uploadMultipleFiles(files, description);
    }

    @GetMapping("/{fileName}")
    public ApiResponse<FileResponse> getFileByName(@PathVariable("fileName") String fileName) {
        ApiResponse<FileResponse> fileFound = privateFileStorageServiceImp.getFileByName(fileName);
        if (fileFound == null) {
            return ApiResponse.fail(HttpStatus.NOT_FOUND);
        }

        return fileFound;
    }

    @GetMapping("/search/keyword")
    public PageApiResponse<FileResponse>  searchFile(@ParameterObject FileSearchRequest fileSearchRequest) {
        PageApiResponse<FileResponse> fileFound = privateFileStorageServiceImp.searchFile(fileSearchRequest);

        return PageApiResponse.of(fileFound.getData(),
                fileFound.getPage().getPageIndex(),
                fileFound.getPage().getPageSize(),
                fileFound.getPage().getTotal());
    }

    @PutMapping("/{fileName}")
    public ApiResponse<FileResponse> updateFile(
            @PathVariable("fileName") String fileName,
            @ParameterObject FileUpdateRequest fileUpdateRequest) {

        return privateFileStorageServiceImp.updateFile(fileName, fileUpdateRequest);
    }

    @DeleteMapping("/{fileName}")
    public ApiResponse<Object> deleteFile(@PathVariable("fileName") String fileName) {
        privateFileStorageServiceImp.deletedFile(fileName);

        return ApiResponse.ok().success("File deleted");
    }

    @GetMapping(path = "/view/{image}")
    ResponseEntity<byte[]> viewImage(
            @PathVariable(value = "image") String image,
            @RequestParam(value = "ratio", required = false) Double ratio,
            @RequestParam(value = "height", required = false) Integer height,
            @RequestParam(value = "width", required = false) Integer width) {

        return privateFileStorageServiceImp.viewImage(image, ratio, height, width);
    }
}
