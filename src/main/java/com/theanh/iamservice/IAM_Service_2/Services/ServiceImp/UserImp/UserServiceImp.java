package com.theanh.iamservice.IAM_Service_2.Services.ServiceImp.UserImp;

import com.theanh.iamservice.IAM_Service_2.AuditorAware.AuditorAwareImp;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.User.PasswordChangeRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.User.PasswordResetRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.User.ProfileUpdateRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.User.ProfileResponse;
import com.theanh.iamservice.IAM_Service_2.Entities.UserEntity;
import com.theanh.iamservice.IAM_Service_2.Exception.AppException;
import com.theanh.iamservice.IAM_Service_2.Exception.ErrorCode;
import com.theanh.iamservice.IAM_Service_2.Jwts.JwtUtil;
import com.theanh.iamservice.IAM_Service_2.Mappers.UserMapper;
import com.theanh.iamservice.IAM_Service_2.Repositories.UserRepository;
import com.theanh.iamservice.IAM_Service_2.Services.IUserService;
import com.theanh.iamservice.IAM_Service_2.Services.ServiceImp.EmailImp.EmailServiceImp;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements IUserService {

    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private final AuditorAwareImp auditorAwareImp;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailServiceImp emailServiceImp;

    private final static long REFRESH_PASSWORD_TOKEN_EXPIRATION = 1000 * 60 * 5;

    private String getCurrentUserEmail() {
         return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    //run well
    @Override
    public ProfileResponse myProfile() {
        String email = getCurrentUserEmail();
        UserEntity userEntity = userRepository.findByEmailAddress(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toProfileResponse(userEntity);
    }

    //run well
    @Override
    public ProfileResponse updateProfile(ProfileUpdateRequest profileUpdateRequest) {
        String email = getCurrentUserEmail();
        UserEntity userEntity = userRepository.findByEmailAddress(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (profileUpdateRequest == null) {
            throw new AppException(ErrorCode.FIELD_MISSING);
        }

        userEntity.setUsername(profileUpdateRequest.getUsername());
        userEntity.setFirstname(profileUpdateRequest.getFirstname());
        userEntity.setLastname(profileUpdateRequest.getLastname());
        userEntity.setAddress(profileUpdateRequest.getAddress());
        userEntity.setPhoneNumber(profileUpdateRequest.getPhoneNumber());
        userEntity.setDateOfBirth(profileUpdateRequest.getDateOfBirth());

        String currentAuditor = auditorAwareImp.getCurrentAuditor().orElse("Unknown");
        userEntity.setLastModifiedBy(currentAuditor);
        userEntity.setLastModifiedAt(LocalDateTime.now());

        userRepository.save(userEntity);

        return userMapper.toProfileResponse(userEntity);
    }

    //run well
    @Override
    public String changePassword(PasswordChangeRequest passwordChangeRequest) {
        String email = getCurrentUserEmail();
        UserEntity userEntity = userRepository.findByEmailAddress(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (passwordChangeRequest == null) {
            throw new AppException(ErrorCode.FIELD_MISSING);
        }

        if (!passwordEncoder.matches(passwordChangeRequest.getOldPassword(), userEntity.getPassword())) {
            throw new AppException(ErrorCode.PASSWORD_MISMATCH);
        }

        if (!passwordChangeRequest.getNewPassword().equals(passwordChangeRequest.getConfirmationPassword())) {
            throw new AppException(ErrorCode.PASSWORD_MISMATCH);
        }

        userEntity.setPassword(passwordEncoder.encode(passwordChangeRequest.getNewPassword()));

        userRepository.save(userEntity);

        return "Password changed successfully.";
    }

    //run well
    @Override
    public String forgotPassword(String email) {
        UserEntity userEntity = userRepository.findByEmailAddress(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        try {
            String resetPasswordToken = jwtUtil.generateToken(userEntity, REFRESH_PASSWORD_TOKEN_EXPIRATION);
            emailServiceImp.sendResetPasswordEmail(email, resetPasswordToken);
        } catch (Exception e) {
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        return "Reset instructions have been sent to your email address. " +
                "Check your inbox or spam folder.";
    }

    //error
    @Override
    public String resetPassword(PasswordResetRequest passwordResetRequest) {
        String email = getCurrentUserEmail();
        UserEntity userEntity = userRepository.findByEmailAddress(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        userEntity.setPassword(passwordEncoder.encode(passwordResetRequest.getResetPassword()));
        userRepository.save(userEntity);
        return "Your password has been reset. " +
                "You can now log in with your new password";
    }
}
