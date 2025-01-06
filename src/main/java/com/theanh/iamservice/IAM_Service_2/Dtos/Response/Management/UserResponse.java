package com.theanh.iamservice.IAM_Service_2.Dtos.Response.Management;

import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Admin.RoleResponse;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private UUID id;

    private String username;
    private String emailAddress;
    private String password;

    private String firstname;
    private String lastname;
    private String address;
    private String phoneNumber;
    private LocalDate dateOfBirth;

    private String secretKey;
    private boolean isVerified;

    private boolean isDeleted;
    private boolean isBanned;

    private String createdBy;
    private LocalDateTime createdAt;
    private String lastModifiedBy;
    private LocalDateTime lastModifiedAt;

    private List<RoleResponse> roleResponses;
}
