package com.theanh.iamservice.IAM_Service_2.Services;

import com.theanh.iamservice.IAM_Service_2.Dtos.Request.User.PasswordChangeRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.User.PasswordResetRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.User.ProfileUpdateRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.User.ProfileResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface IUserService {

    ProfileResponse myProfile();

    ProfileResponse updateProfile(ProfileUpdateRequest profileUpdateRequest);

    String changePassword(PasswordChangeRequest passwordChangeRequest);

    String forgotPassword(String email);

    String resetPassword(PasswordResetRequest passwordResetRequest);
}
