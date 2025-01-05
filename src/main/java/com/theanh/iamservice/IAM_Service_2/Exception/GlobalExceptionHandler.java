package com.theanh.iamservice.IAM_Service_2.Exception;

import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Api.ApiResponse;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Api.ApiResponseBuilder;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Hidden
@RestControllerAdvice
public class GlobalExceptionHandler {

//    @ExceptionHandler(value = RuntimeException.class)
//    public ResponseEntity<ApiResponse<?>> handlingRuntimeException(RuntimeException exception) {
//
//        return ResponseEntity.status(ErrorCode.UNCATEGORIZED_EXCEPTION.getHttpStatus())
//                .body(ApiResponseBuilder
//                .buildErrorResponse(ErrorCode.UNCATEGORIZED_EXCEPTION.getHttpStatus(),
//                ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage()));
//    }

    @ExceptionHandler(value = AppException.class)
    public ResponseEntity<ApiResponse<?>> handlingAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();

        return ResponseEntity.status(errorCode.getHttpStatus()).body(ApiResponseBuilder
                .buildErrorResponse(errorCode.getHttpStatus(),
                        errorCode.getMessage()));
    }

//    @ExceptionHandler(value = MethodArgumentNotValidException.class)
//    public ResponseEntity<ApiResponse<?>> handlingMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
//        String key = exception.getFieldError().getDefaultMessage();
//        ErrorCode errorCode = ErrorCode.valueOf(key);
//
//        return ResponseEntity.status(errorCode.getHttpStatus()).body(ApiResponseBuilder
//                .buildErrorResponse(errorCode.getHttpStatus(),
//                        errorCode.getMessage()));
//    }
//
//    @ExceptionHandler(value = AccessDeniedException.class)
//    public ResponseEntity<ApiResponse<?>> handlingAccessDeniedException(AccessDeniedException exception) {
//        ErrorCode errorCode = ErrorCode.INSUFFICIENT_PERMISSIONS;
//
//        return ResponseEntity.status(errorCode.getHttpStatus()).body(ApiResponseBuilder
//                .buildErrorResponse(errorCode.getHttpStatus(),
//                        errorCode.getMessage()));
//    }

//    private void catchException(Exception exception) {
//        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
//        if (Objects.nonNull(requestAttributes)) {
//            requestAttributes.setAttribute(
//                    "custom_exception_atr", exception, RequestAttributes.SCOPE_REQUEST);
//        }
//    }

}
