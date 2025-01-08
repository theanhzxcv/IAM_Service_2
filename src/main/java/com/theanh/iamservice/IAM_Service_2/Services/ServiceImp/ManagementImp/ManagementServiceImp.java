package com.theanh.iamservice.IAM_Service_2.Services.ServiceImp.ManagementImp;

import com.theanh.iamservice.IAM_Service_2.AuditorAware.AuditorAwareImp;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Management.UserCreationRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Management.UserSearchRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Management.UserUpdateRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Admin.PermissionResponse;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Admin.RoleResponse;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Management.SearchResponse;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Management.UserResponse;
import com.theanh.iamservice.IAM_Service_2.Entities.*;
import com.theanh.iamservice.IAM_Service_2.Exception.AppException;
import com.theanh.iamservice.IAM_Service_2.Exception.ErrorCode;
import com.theanh.iamservice.IAM_Service_2.Keycloak.KeycloakProperties;
import com.theanh.iamservice.IAM_Service_2.Mappers.RoleMapper;
import com.theanh.iamservice.IAM_Service_2.Mappers.UserMapper;
import com.theanh.iamservice.IAM_Service_2.Repositories.*;
import com.theanh.iamservice.IAM_Service_2.Repositories.RepositoryImp.UserRepositoryImp;
import com.theanh.iamservice.IAM_Service_2.Services.IManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ManagementServiceImp implements IManagementService {
    private final RoleMapper roleMapper;
    private final UserMapper userMapper;
    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRoleRepository userRoleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditorAwareImp auditorAwareImp;
    private final UserRepositoryImp userRepositoryImp;
    private final KeycloakProperties keycloakProperties;

    private String getAdminToken() {
        String tokenUrl = keycloakProperties.getAuthServerUrl()
                + "/realms/"
                + keycloakProperties.getRealm()
                + "/protocol/openid-connect/token";

        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("client_id", keycloakProperties.getClientId());
        body.add("client_secret", keycloakProperties.getClientSecret());
        body.add("username", keycloakProperties.getAdminUsername());
        body.add("password", keycloakProperties.getAdminPassword());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.postForEntity(
                tokenUrl,
                new org.springframework.http.HttpEntity<>(body, headers),
                Map.class
        );

        return (String) response.getBody().get("access_token");
    }

    @Override
    public UserResponse createNewUser(UserCreationRequest userCreationRequest) {
        if (userRepository.findByUsername(userCreationRequest.getUsername()).isPresent()
                || userRepository.findByEmailAddress(userCreationRequest.getEmailAddress()).isPresent()) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        UserEntity userEntity = saveUserEntity(userCreationRequest);

        List<RoleEntity> validRoles = getValidRoles(userCreationRequest);

        assignRolesToUser(userEntity, validRoles);

        List<RoleResponse> roleResponses = mapRolesToRoleResponses(validRoles);

        return userMapper.toUserResponse(userEntity, roleResponses);
    }

    private UserEntity saveUserEntity(UserCreationRequest userCreationRequest) {
        UserEntity userEntity = userMapper.toUserEntity(userCreationRequest);
        userEntity.setPassword(passwordEncoder.encode(userCreationRequest.getPassword()));
        saveUserToKeycloak(userCreationRequest.getUsername(),
                userCreationRequest.getEmailAddress(),
                userCreationRequest.getPassword(),
                userCreationRequest.getFirstname(),
                userCreationRequest.getLastname());

        String currentAuditor = auditorAwareImp.getCurrentAuditor().orElse("Unknown");
        userEntity.setCreatedBy(currentAuditor);
        userEntity.setCreatedAt(LocalDateTime.now());
        userEntity.setLastModifiedBy(currentAuditor);
        userEntity.setLastModifiedAt(LocalDateTime.now());

        return userRepository.save(userEntity);
    }

    private void saveUserToKeycloak(String username,
                                    String email,
                                    String firstname,
                                    String lastname,
                                    String password) {
        String adminToken = getAdminToken();
        String registerUrl = keycloakProperties.getAuthServerUrl()
                + "/admin/realms/"
                + keycloakProperties.getRealm()
                + "/users";

        Map<String, Object> userPayload = Map.of(
                "username", username,
                "email", email,
                "firstName", firstname,
                "lastName", lastname,
                "enabled", true,
                "credentials", List.of(
                        Map.of(
                                "type", "password",
                                "value", password,
                                "temporary", false
                        )
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminToken);

        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> responseBody = restTemplate.postForEntity(
                    registerUrl,
                    new HttpEntity<>(userPayload, headers),
                    String.class
            );

            if (!responseBody.getStatusCode().is2xxSuccessful()) {
//                throw new AppException(ErrorCode.REGISTRATION_FAILED);
            }

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 409) {
                throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
            }
        }
    }

    private List<RoleEntity> getValidRoles(UserCreationRequest userCreationRequest) {
        List<RoleEntity> roles = roleRepository.findAllById(userCreationRequest.getRoles());

        if (roles.size() != userCreationRequest.getRoles().size()) {
            RoleEntity defaultRole = roleRepository.findByName("USER")
                    .orElseThrow(() -> new RuntimeException("Default role USER does not exist"));
            roles.add(defaultRole);
        }

        List<RoleEntity> validRoles = roles.stream()
                .filter(role -> !role.isDeleted())
                .collect(Collectors.toList());

        if (validRoles.size() != roles.size()) {
            throw new RuntimeException("Some roles are marked as deleted");
        }

        return validRoles;
    }

    private void assignRolesToUser(UserEntity userEntity, List<RoleEntity> validRoles) {
        List<UserRole> userRoles = validRoles.stream()
                .map(role -> UserRole.builder()
                        .userId(userEntity.getId())
                        .roleName(role.getName())
                        .build())
                .collect(Collectors.toList());

        userRoleRepository.saveAll(userRoles);
    }

    private List<RoleResponse> mapRolesToRoleResponses(List<RoleEntity> validRoles) {
        return validRoles.stream()
                .map(role -> {
                    List<PermissionEntity> permissions = getRolePermissions(role);
                    return roleMapper.toRoleResponseWithPermission(role, permissions);
                })
                .collect(Collectors.toList());
    }

    private List<PermissionEntity> getRolePermissions(RoleEntity role) {
        return rolePermissionRepository.findByRoleName(role.getName()).stream()
                .map(rolePermission -> permissionRepository.findByName(rolePermission.getPermissionName())
                        .orElseThrow(() -> new RuntimeException("Permission not found")))
                .collect(Collectors.toList());
    }

    @Override
    public Page<UserResponse> allUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<UserEntity> userPage = userRepository.findAll(pageable);

        return userPage.map(userMapper::toUserResponse);
    }

    @Override
    public Page<SearchResponse> findUserByKeyWord(UserSearchRequest userSearchRequest) {
        List<UserEntity> userEntities = userRepositoryImp.search(userSearchRequest);

        Long totalCount = userRepositoryImp.count(userSearchRequest);

        List<SearchResponse> userResponses = userEntities.stream()
                .map(userMapper::toSearchResponse)
                .toList();

        return new PageImpl<>(
                userResponses,
                PageRequest.of(userSearchRequest.getPageIndex() - 1, userSearchRequest.getPageSize()),
                totalCount
        );
    }

    @Override
    public String changePassword(String email, String newPassword) {
        String adminToken = getAdminToken();
        String userId = getUserIdByEmail(email);

        String url = keycloakProperties.getAuthServerUrl() +
                "/admin/realms/" +
                keycloakProperties.getRealm() +
                "/users/" +
                userId +
                "/reset-password";

        Map<String, Object> passwordPayload = Map.of(
                "type", "password",
                "value", newPassword,
                "temporary", false
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminToken);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(passwordPayload, headers);

        try {
            restTemplate.exchange(url, HttpMethod.PUT, requestEntity, String.class);
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Failed to change password: " + e.getMessage(), e);
        }
        return "Change password successfully";
    }

    @Override
    public UserResponse updateUser(UserUpdateRequest userUpdateRequest) {
        if (userUpdateRequest.getEmail() == null
                || userUpdateRequest.getUsername() == null
                || userUpdateRequest.getFirstname() == null
                || userUpdateRequest.getLastname() == null) {
            throw new AppException(ErrorCode.FIELD_MISSING);
        }

        UserEntity userEntity = userRepository.findByEmailAddress(userUpdateRequest.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        userEntity.setUsername(userUpdateRequest.getUsername());
        userEntity.setFirstname(userUpdateRequest.getFirstname());
        userEntity.setLastname(userUpdateRequest.getLastname());

        userEntity.setBanned(!"No".equalsIgnoreCase(userUpdateRequest.getIsBanned()));
        userEntity.setDeleted(!"No".equalsIgnoreCase(userUpdateRequest.getIsDeleted()));

        String currentAuditor = auditorAwareImp.getCurrentAuditor().orElse("Unknown");
        userEntity.setLastModifiedBy(currentAuditor);
        userEntity.setLastModifiedAt(LocalDateTime.now());
        userRepository.save(userEntity);

        String userId = getUserIdByEmail(userUpdateRequest.getEmail());
        updateUserInKeycloak(userId,
                userUpdateRequest.getUsername(),
                userUpdateRequest.getEmail(),
                userUpdateRequest.getFirstname(),
                userUpdateRequest.getLastname());

        if (userUpdateRequest.getRoles() != null && !userUpdateRequest.getRoles().isEmpty()) {
            updateUserRoles(userEntity, userUpdateRequest.getRoles());
        }

        userRepository.save(userEntity);

        List<RoleResponse> roleResponses = getUserRolesWithPermissions(userEntity);

        return userMapper.toUserResponse(userEntity, roleResponses);
    }

    private String getUserIdByEmail(String email) {
        String adminToken = getAdminToken();  // Assume you have a method to get the admin token
        String searchUrl = keycloakProperties.getAuthServerUrl()
                + "/admin/realms/"
                + keycloakProperties.getRealm()
                + "/users?email=" + email;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List> response = restTemplate.exchange(
                searchUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                List.class
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null && !response.getBody().isEmpty()) {
            Map<String, Object> user = (Map<String, Object>) response.getBody().get(0);
            return user.get("id").toString();  // Return the user's ID
        } else {
            throw new AppException(ErrorCode.USER_NOT_FOUND);  // Handle user not found
        }
    }

    private void updateUserInKeycloak(String userId,
                                      String username,
                                      String email,
                                      String firstname,
                                      String lastname) {
        String adminToken = getAdminToken();
        String updateUrl = keycloakProperties.getAuthServerUrl()
                + "/admin/realms/"
                + keycloakProperties.getRealm()
                + "/users/" + userId;

        Map<String, Object> userPayload = new HashMap<>();
        userPayload.put("username", username);
        userPayload.put("email", email);
        userPayload.put("firstName", firstname);
        userPayload.put("lastName", lastname);
        userPayload.put("enabled", true);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminToken);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    updateUrl,
                    HttpMethod.PUT,
                    new HttpEntity<>(userPayload, headers),
                    String.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Failed to update user. HTTP Status: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            String responseBody = e.getResponseBodyAsString();
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new RuntimeException("User not found: " + responseBody, e);
            } else {
                throw new RuntimeException("Error updating user: " + responseBody, e);
            }
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error: " + e.getMessage(), e);
        }
    }

    private void updateUserRoles(UserEntity userEntity, List<String> roleNames) {
        List<UserRole> existingUserRole = userRoleRepository.findByUserId(userEntity.getId());

        List<RoleEntity> roles = roleRepository.findAllById(roleNames);

        if (roles.size() != roleNames.size()) {
            RoleEntity defaultRole = roleRepository.findByName("USER")
                    .orElseThrow(() -> new RuntimeException("Default role USER does not exist"));
            roles.add(defaultRole);
        }

        List<RoleEntity> validRoles = roles.stream()
                .filter(role -> !role.isDeleted())
                .toList();

        if (validRoles.size() != roles.size()) {
            throw new RuntimeException("Some roles are marked as deleted");
        }

        List<UserRole> userRoles = validRoles.stream()
                .filter(role -> !existingUserRole.contains(role.getName()))
                .map(role -> UserRole.builder()
                        .userId(userEntity.getId())
                        .roleName(role.getName())
                        .build())
                .collect(Collectors.toList());

        userRoleRepository.saveAll(userRoles);
    }

    private List<RoleResponse> getUserRolesWithPermissions(UserEntity userEntity) {
        List<UserRole> userRoles = userRoleRepository.findByUserId(userEntity.getId());

        return userRoles.stream()
                .map(userRole -> {
                    RoleEntity role = getRoleByName(userRole.getRoleName());
                    List<PermissionResponse> permissionResponses = getPermissionsForRole(role);
                    return roleMapper.toRoleResponseWithPermissions(role, permissionResponses);
                })
                .collect(Collectors.toList());
    }

    private RoleEntity getRoleByName(String roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found"));
    }

    private List<PermissionResponse> getPermissionsForRole(RoleEntity role) {
        List<RolePermission> rolePermissions = rolePermissionRepository.findByRoleName(role.getName());

        return rolePermissions.stream()
                .map(rolePermission -> {
                    PermissionEntity permission = getPermissionByName(rolePermission.getPermissionName());
                    return new PermissionResponse(permission.getName(), permission.getResource(), permission.getScope(), permission.isDeleted());
                })
                .collect(Collectors.toList());
    }

    private PermissionEntity getPermissionByName(String permissionName) {
        return permissionRepository.findByName(permissionName)
                .orElseThrow(() -> new RuntimeException("Permission not found"));
    }

    @Override
    public UserResponse banUser(String emailAddress) {
        UserEntity userEntity = userRepository.findByEmailAddress(emailAddress)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        userEntity.setBanned(true);
        userRepository.save(userEntity);
        return userMapper.toUserResponse(userEntity);
    }

    @Override
    public UserResponse deleteUser(String emailAddress) {
        UserEntity userEntity = userRepository.findByEmailAddress(emailAddress)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        userEntity.setDeleted(true);
        userRepository.save(userEntity);
        return userMapper.toUserResponse(userEntity);
    }
}
