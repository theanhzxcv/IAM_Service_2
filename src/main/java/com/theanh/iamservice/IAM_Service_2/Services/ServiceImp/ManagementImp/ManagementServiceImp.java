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
import com.theanh.iamservice.IAM_Service_2.Mappers.RoleMapper;
import com.theanh.iamservice.IAM_Service_2.Mappers.UserMapper;
import com.theanh.iamservice.IAM_Service_2.Repositories.*;
import com.theanh.iamservice.IAM_Service_2.Repositories.RepositoryImp.UserRepositoryImp;
import com.theanh.iamservice.IAM_Service_2.Services.IManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ManagementServiceImp implements IManagementService {
    private final RoleMapper roleMapper;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRoleRepository userRoleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditorAwareImp auditorAwareImp;
    private final UserRepositoryImp userRepositoryImp;


    @Override
    public UserResponse createNewUser(UserCreationRequest userCreationRequest) {
        UserEntity userEntity = saveUserEntity(userCreationRequest);

        List<RoleEntity> validRoles = getValidRoles(userCreationRequest);

        assignRolesToUser(userEntity, validRoles);

        List<RoleResponse> roleResponses = mapRolesToRoleResponses(validRoles);

        return userMapper.toUserResponse(userEntity, roleResponses);
    }

    private UserEntity saveUserEntity(UserCreationRequest userCreationRequest) {
        UserEntity userEntity = userMapper.toUserEntity(userCreationRequest);
        userEntity.setPassword(passwordEncoder.encode(userCreationRequest.getPassword()));

        String currentAuditor = auditorAwareImp.getCurrentAuditor().orElse("Unknown");
        userEntity.setCreatedBy(currentAuditor);
        userEntity.setCreatedAt(LocalDateTime.now());

        userEntity.setLastModifiedBy(currentAuditor);
        userEntity.setLastModifiedAt(LocalDateTime.now());

        return userRepository.save(userEntity);
    }

    private List<RoleEntity> getValidRoles(UserCreationRequest userCreationRequest) {
        List<RoleEntity> roles = roleRepository.findAllById(userCreationRequest.getRoles());

        if (roles.size() != userCreationRequest.getRoles().size()) {
            throw new RuntimeException("Some roles do not exist");
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
    public UserResponse updateUser(String emailAddress, UserUpdateRequest userUpdateRequest) {
        UserEntity userEntity = getUserByEmail(emailAddress);

        if (userUpdateRequest == null) {
            throw new AppException(ErrorCode.FIELD_MISSING);
        }
        updateUserEntity(userEntity, userUpdateRequest);

        if (userUpdateRequest.getRoles() != null) {
            updateUserRoles(userEntity, userUpdateRequest.getRoles());
        }

        userRepository.save(userEntity);

        List<RoleResponse> roleResponses = getUserRolesWithPermissions(userEntity);

        return userMapper.toUserResponse(userEntity, roleResponses);
    }

    private UserEntity getUserByEmail(String emailAddress) {
        return userRepository.findByEmailAddress(emailAddress)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    private void updateUserEntity(UserEntity userEntity, UserUpdateRequest userUpdateRequest) {
        userEntity = userMapper.toUserEntity(userUpdateRequest);
        if (userUpdateRequest.getIsBanned().equals("false")){
            userEntity.setBanned(false);
        }
        if (userUpdateRequest.getIsDeleted().equals("false")){
            userEntity.setDeleted(false);
        }

        String currentAuditor = auditorAwareImp.getCurrentAuditor().orElse("Unknown");
        userEntity.setLastModifiedBy(currentAuditor);
        userEntity.setLastModifiedAt(LocalDateTime.now());
    }

    private void updateUserRoles(UserEntity userEntity, List<String> roleNames) {
        List<RoleEntity> roles = roleRepository.findAllById(roleNames);

        if (roles.size() != roleNames.size()) {
            throw new RuntimeException("Some roles do not exist");
        }

        List<RoleEntity> validRoles = roles.stream()
                .filter(role -> !role.isDeleted())
                .toList();

        if (validRoles.size() != roles.size()) {
            throw new RuntimeException("Some roles are marked as deleted");
        }

        List<UserRole> userRoles = validRoles.stream()
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
