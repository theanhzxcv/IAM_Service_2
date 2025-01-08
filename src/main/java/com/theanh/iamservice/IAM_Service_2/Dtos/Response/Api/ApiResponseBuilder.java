package com.theanh.iamservice.IAM_Service_2.Dtos.Response.Api;

import org.springframework.http.HttpStatus;

import java.sql.Timestamp;

public class ApiResponseBuilder {

    public static <T> ApiResponse<T> buildSuccessResponse(String message, T data) {
        return ApiResponse.<T>builder()
                .code(200)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> createdSuccessResponse(String message, T data) {
        return ApiResponse.<T>builder()
                .code(201)
                .message(message)
                .data(data)
                .build();
    }
    public static <T> ApiResponse<T> buildErrorResponse(HttpStatus status, String message) {
        return ApiResponse.<T>builder()
                .code(status.value())
                .message(message)
                .build();
    }
}
