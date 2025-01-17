package com.theanh.iamservice.IAM_Service_2.Dtos.Response.Api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> implements Serializable {
    protected T data;
    private boolean success;
    private int code;
    private String message;
    private long timestamp;

    public ApiResponse() {
        timestamp = Instant.now().toEpochMilli();
        success = true;
    }

    public ApiResponse(String s) {
    }

    public static <T> ApiResponse<T> of(T res) {
        ApiResponse<T> apiResponse = new ApiResponse<>();
        apiResponse.data = res;
        apiResponse.success();
        return apiResponse;
    }

    public static <T> ApiResponse<T> created(T res) {
        ApiResponse<T> apiResponse = new ApiResponse<>();
        apiResponse.data = res;
        apiResponse.created();
        return apiResponse;
    }

    public static <T> ApiResponse<T> ok() {
        ApiResponse<T> apiResponse = new ApiResponse<>();
        apiResponse.success();
        return apiResponse;
    }

    public static <T> ApiResponse<T> fail(HttpStatus httpStatus) {
        ApiResponse<T> apiResponse = new ApiResponse<>();
        apiResponse.setCode(httpStatus.value());
        apiResponse.setSuccess(false);
        return apiResponse;
    }

    public void success() {
        success = true;
        code = 200;
    }

    public void created() {
        success = true;
        code = 201;
    }

    public ApiResponse<T> data(T res) {
        data = res;
        return this;
    }

    public ApiResponse<T> success(String message) {
        success = true;
        this.message = message;
        code = 200;
        return this;
    }

    public ApiResponse<T> fail(String message) {
        success = false;
        this.message = message;
        return this;
    }
}
