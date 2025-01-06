package com.theanh.iamservice.IAM_Service_2.Dtos.Response.Api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private int code;
    private String status;
    private Timestamp timestamp;
    private String message;
    private T data;
}
