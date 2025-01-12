package com.theanh.iamservice.IAM_Service_2.Exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    INTERNAL_SERVER_ERROR("Unexpected error occurred! Please try again later.",
            HttpStatus.INTERNAL_SERVER_ERROR),
    SESSION_EXPIRED("Session expired! Please log in again.",
            HttpStatus.UNAUTHORIZED),
    FIELD_MISSING("Required fields are missing! Please ensure all fields are filled.",
            HttpStatus.BAD_REQUEST),  // Fixed status to BAD_REQUEST
    PASSWORD_POLICY_VIOLATION("Password must be at least 8 characters long " +
            "and contain a number, " +
            "uppercase letter, " +
            "and special character.",
            HttpStatus.BAD_REQUEST),  // Fixed status to BAD_REQUEST
    INVALID_OTP("The OTP is invalid! Please check and try again.",
            HttpStatus.BAD_REQUEST),  // Fixed status to BAD_REQUEST
    PASSWORD_MISMATCH("Passwords do not match! Please verify both entries.",
            HttpStatus.BAD_REQUEST),  // Fixed status to BAD_REQUEST
    EMAIL_ALREADY_EXISTS("The email or username is already in use! " +
            "If you've forgotten your password, please try account recovery.",
            HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND("No account found with that email address! Please check or create a new account.",
            HttpStatus.NOT_FOUND),
    AUTHENTICATION_FAILED("Sign-in failed! Please verify your email and password.",
            HttpStatus.UNAUTHORIZED),
    INSUFFICIENT_PERMISSIONS("You do not have permission to perform this action.",
            HttpStatus.FORBIDDEN),
    REGISTRATION_FAILED("Sign-up failed! Please try again.",
            HttpStatus.BAD_REQUEST),
    USER_DEACTIVATED("Your account is either deleted or banned! Please contact support.",
            HttpStatus.FORBIDDEN),
    PERMISSION_ALREADY_EXISTS("This permission already exists. Please choose a different permission name.",
            HttpStatus.BAD_REQUEST),
    PERMISSION_NOT_FOUND("The specified permission was not found. Please check the permission name.",
            HttpStatus.NOT_FOUND),
    PERMISSION_DELETED("This permission has been deleted and is no longer available! " +
            "Please check or choose another permission.", HttpStatus.FORBIDDEN),
    ROLE_ALREADY_EXISTS("This role already exists. Please choose a different role name.",
            HttpStatus.BAD_REQUEST),
    ROLE_NOT_FOUND("The specified role was not found. Please check the role name.",
            HttpStatus.NOT_FOUND),
    ROLE_DELETED("This role has been deleted and is no longer available! " +
            "Please check or choose another role.",
            HttpStatus.FORBIDDEN)

    ;

    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
