package com.theanh.iamservice.IAM_Service_2.Repositories.FeignClients;

import com.theanh.iamservice.IAM_Service_2.Config.FeignClientConfig;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.File.FileSearchRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.File.FileUpdateRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Api.ApiResponse;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Api.PageApiResponse;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.File.FileResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(
        name = "public-storage-service",
        url = "${app.services.storage.public}",
        configuration = FeignClientConfig.class
)
public interface PublicFeignClient {

    Logger logger = LoggerFactory.getLogger(PublicFeignClient.class);

    @CircuitBreaker(name = "storage", fallbackMethod = "fallbackMethod")
    @Retry(name = "storage")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<FileResponse> uploadFile(
            @RequestPart(value = "file") MultipartFile file,
            @RequestPart(value = "description") String description);

    @PostMapping(path = "/multiple-files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<List<FileResponse>> uploadMultipleFiles(
            @RequestPart(value = "files") List<MultipartFile> files,
            @RequestPart(value = "description", required = false) String description);

    @CircuitBreaker(name = "storage", fallbackMethod = "fallbackMethod")
    @Retry(name = "storage")
    @GetMapping(path = "/{fileName}")
    ApiResponse<FileResponse> getFileByName(@PathVariable(value = "fileName") String fileName);

    @GetMapping(path = "/search/keyword")
    PageApiResponse<FileResponse> searchFile(@SpringQueryMap FileSearchRequest fileSearchRequest);

    @PutMapping(path = "/{fileName}", consumes = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<FileResponse> updateFile(
            @PathVariable(value = "fileName") String fileName,
            @RequestBody FileUpdateRequest fileUpdateRequest);

    @DeleteMapping(path = "/{fileName}")
    ApiResponse<Object> deleteFile(
            @PathVariable(value = "fileName") String fileName);

    default ApiResponse<Object> fallbackMethod(String fileName, Throwable throwable) {
        logger.info("Cannot get information from file {}, failure reason: {}", fileName, throwable.getMessage());

        // Create and return a fallback ApiResponse<FileResponse> (could be an error response)
        return ApiResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR).fail("Error fetching file: " + fileName);
    }

}

