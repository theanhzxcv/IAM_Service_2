package com.theanh.iamservice.IAM_Service_2.Exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    INTERNAL_SERVER_ERROR("Unexpected error occurred. Please try again later.",
            HttpStatus.INTERNAL_SERVER_ERROR),
    SESSION_EXPIRED("Session expired. Please log in again.",
            HttpStatus.UNAUTHORIZED),
    FIELD_MISSING("Required fields are missing. Please ensure all fields are filled.",
            HttpStatus.UNPROCESSABLE_ENTITY),
    PASSWORD_POLICY_VIOLATION("Password must be at least 8 characters long and contain a number, " +
            "uppercase letter, and special character.",
            HttpStatus.UNPROCESSABLE_ENTITY),
    INVALID_OTP("The OTP is invalid. Please check and try again.",
            HttpStatus.UNPROCESSABLE_ENTITY),
    PASSWORD_MISMATCH("Passwords do not match. Please verify both entries.",
            HttpStatus.UNPROCESSABLE_ENTITY),
    EMAIL_ALREADY_EXISTS("The email or username is already in use. " +
            "If you've forgotten your password, please try account recovery.",
            HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND("No account found with that email address. Please check or create a new account.",
            HttpStatus.NOT_FOUND),
    AUTHENTICATION_FAILED("Login failed. Please verify your email and password.",
            HttpStatus.UNAUTHORIZED),
    INSUFFICIENT_PERMISSIONS("You do not have permission to perform this action.",
            HttpStatus.FORBIDDEN),
    REGISTRATION_FAILED("Sign-up failed. Please try again.",
            HttpStatus.BAD_REQUEST),
    LOGIN_FAILED("Sign-in failed. Please verify your credentials and try again.",
            HttpStatus.UNPROCESSABLE_ENTITY),
    USER_DEACTIVATED("Your account is either deleted or banned. Please contact support.",
            HttpStatus.FORBIDDEN)

    ;

    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
