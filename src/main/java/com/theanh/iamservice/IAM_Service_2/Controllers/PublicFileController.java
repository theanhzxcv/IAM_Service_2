package com.theanh.iamservice.IAM_Service_2.Controllers;

import com.theanh.iamservice.IAM_Service_2.Dtos.Request.File.FileSearchRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.File.FileUpdateRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Api.ApiResponse;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Api.PageApiResponse;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.File.FileResponse;
import com.theanh.iamservice.IAM_Service_2.Services.ServiceImp.FileStorageImp.PrivateFileStorageServiceImp;
import com.theanh.iamservice.IAM_Service_2.Services.ServiceImp.FileStorageImp.PublicFileStorageServiceImp;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files/public")
@Tag(name = "PublicFileStorage")
public class PublicFileController {
    private static final Logger logger = LoggerFactory.getLogger(PublicFileController.class);
    private final PublicFileStorageServiceImp publicFileStorageServiceImp;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<FileResponse> uploadFile(@RequestParam("file") MultipartFile file,
                                                             @RequestParam("description") String description) {

        return publicFileStorageServiceImp.uploadFile(file, description);
    }

    @PostMapping(path = "/multiple-files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<List<FileResponse>> uploadMultipleFile(
            @RequestParam("file") List<MultipartFile> files,
            @RequestParam("description") String description) {

        return publicFileStorageServiceImp.uploadMultipleFiles(files, description);
    }

    @GetMapping("/{fileName}")
    public ApiResponse<FileResponse> getFileByName(@PathVariable("fileName") String fileName) {
        ApiResponse<FileResponse> fileFound = publicFileStorageServiceImp.getFileByName(fileName);

        if (fileFound == null) {
            return ApiResponse.fail(HttpStatus.NOT_FOUND);
        }
        return fileFound;
    }

    @GetMapping("/search/keyword")
    public PageApiResponse<FileResponse>  searchFile(@ParameterObject FileSearchRequest fileSearchRequest) {
        PageApiResponse<FileResponse> fileFound = publicFileStorageServiceImp.searchFile(fileSearchRequest);

        return PageApiResponse.of(fileFound.getData(),
                fileFound.getPage().getPageIndex(),
                fileFound.getPage().getPageSize(),
                fileFound.getPage().getTotal());
    }

    @PutMapping("/{fileName}")
    public ApiResponse<FileResponse> updateFile(
            @PathVariable("fileName") String fileName,
            @ParameterObject FileUpdateRequest fileUpdateRequest) {

        return publicFileStorageServiceImp.updateFile(fileName, fileUpdateRequest);
    }

    @DeleteMapping("/{fileName}")
    public ApiResponse<Object> deleteFile(@PathVariable("fileName") String fileName) {
        publicFileStorageServiceImp.deletedFile(fileName);

        return ApiResponse.ok().success("File deleted");
    }
}
