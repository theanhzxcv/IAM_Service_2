package com.theanh.iamservice.IAM_Service_2.Dtos.Response.Api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private int code;
    private String status;
    private Timestamp timestamp;
    private String message;
    private T data;
}
