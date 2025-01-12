package com.theanh.iamservice.IAM_Service_2.Services.ServiceImp.AdminImp;

import com.theanh.iamservice.IAM_Service_2.AuditorAware.AuditorAwareImp;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Admin.RoleCreationRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Admin.RoleUpdateRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Admin.PermissionResponse;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Admin.RoleResponse;
import com.theanh.iamservice.IAM_Service_2.Entities.PermissionEntity;
import com.theanh.iamservice.IAM_Service_2.Entities.RoleEntity;
import com.theanh.iamservice.IAM_Service_2.Entities.RolePermission;
import com.theanh.iamservice.IAM_Service_2.Exception.AppException;
import com.theanh.iamservice.IAM_Service_2.Exception.ErrorCode;
import com.theanh.iamservice.IAM_Service_2.Mappers.PermissionMapper;
import com.theanh.iamservice.IAM_Service_2.Mappers.RoleMapper;
import com.theanh.iamservice.IAM_Service_2.Repositories.PermissionRepository;
import com.theanh.iamservice.IAM_Service_2.Repositories.RolePermissionRepository;
import com.theanh.iamservice.IAM_Service_2.Repositories.RoleRepository;
import com.theanh.iamservice.IAM_Service_2.Services.IRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImp implements IRoleService {
    private final RoleMapper roleMapper;
    private final RoleRepository roleRepository;
    private final AuditorAwareImp auditorAwareImp;
    private final PermissionMapper permissionMapper;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;

    @Override
    public RoleResponse createRole(RoleCreationRequest roleCreationRequest) {
        if (roleRepository.findByName(roleCreationRequest.getName()).isPresent()) {
            throw new AppException(ErrorCode.ROLE_ALREADY_EXISTS);
        }

        RoleEntity roleEntity = roleMapper.toRoleEntity(roleCreationRequest);
        roleEntity.setRoot("ADMIN".equalsIgnoreCase(roleCreationRequest.getName()));
        roleEntity.setCreatedBy(auditorAwareImp.getCurrentAuditor().orElse("Unknown"));
        roleEntity.setCreatedAt(LocalDateTime.now());
        roleEntity.setLastModifiedBy(auditorAwareImp.getCurrentAuditor().orElse("Unknown"));
        roleEntity.setLastModifiedAt(LocalDateTime.now());
        roleRepository.save(roleEntity);

        List<PermissionEntity> validPermissions = fetchAndValidatePermissions(roleCreationRequest.getPermissions());
        saveRolePermissions(roleCreationRequest.getName(), validPermissions);

        return buildRoleResponse(roleEntity, validPermissions);
    }

    private List<PermissionEntity> fetchAndValidatePermissions(List<String> permissionNames) {
        List<PermissionEntity> permissions = permissionRepository.findAllById(permissionNames);

        if (permissions.size() != permissionNames.size()) {
            throw new AppException(ErrorCode.PERMISSION_NOT_FOUND);
        }

        List<PermissionEntity> validPermissions = permissions.stream()
                .filter(permission -> !permission.isDeleted())
                .toList();

        if (validPermissions.size() != permissions.size()) {
            throw new AppException(ErrorCode.PERMISSION_DELETED);
        }

        return validPermissions;
    }

    private void saveRolePermissions(String roleName, List<PermissionEntity> validPermissions) {
        List<RolePermission> rolePermissions = validPermissions.stream()
                .map(permission -> RolePermission.builder()
                        .roleName(roleName)
                        .permissionName(permission.getName())
                        .build())
                .collect(Collectors.toList());

        rolePermissionRepository.saveAll(rolePermissions);
    }

    private RoleResponse buildRoleResponse(RoleEntity roleEntity, List<PermissionEntity> validPermissions) {
        List<PermissionResponse> permissionResponses = validPermissions.stream()
                .map(permissionMapper::toPermissionResponse)
                .collect(Collectors.toList());

        return roleMapper.toRoleResponse(roleEntity, permissionResponses);
    }

    @Override
    public RoleResponse updateRole(String name, RoleUpdateRequest roleUpdateRequest) {
        RoleEntity roleEntity = roleRepository.findByName(name)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        if (roleUpdateRequest == null) {
            throw new AppException(ErrorCode.FIELD_MISSING);
        }
        roleEntity.setRoot(!"Yes".equalsIgnoreCase(roleUpdateRequest.getIsRoot()));
        roleEntity.setDeleted(!"No".equalsIgnoreCase(roleUpdateRequest.getIsDeleted()));
        roleEntity.setLastModifiedBy(auditorAwareImp.getCurrentAuditor().orElse("Unknown"));
        roleEntity.setLastModifiedAt(LocalDateTime.now());
        roleRepository.save(roleEntity);

        updateRolePermissions(roleEntity, roleUpdateRequest.getPermissions());

        List<PermissionResponse> permissionResponses = getPermissionResponses(roleEntity);

        return roleMapper.toRoleResponse(roleEntity, permissionResponses);
    }

    private void updateRolePermissions(RoleEntity roleEntity, List<String> permissionNames) {
        List<PermissionEntity> validPermissions = fetchAndValidatePermissions(permissionNames);
        List<RolePermission> existingRolePermissions = rolePermissionRepository.findByRoleName(roleEntity.getName());

        Set<String> existingPermissionNames = existingRolePermissions.stream()
                .map(RolePermission::getPermissionName)
                .collect(Collectors.toSet());

        List<RolePermission> newRolePermissions = validPermissions.stream()
                .filter(permission -> !existingPermissionNames.contains(permission.getName()))
                .map(permission -> RolePermission.builder()
                        .roleName(roleEntity.getName())
                        .permissionName(permission.getName())
                        .build())
                .collect(Collectors.toList());

        rolePermissionRepository.saveAll(newRolePermissions);
    }

    private List<PermissionResponse> getPermissionResponses(RoleEntity roleEntity) {
        List<RolePermission> rolePermissions = rolePermissionRepository.findByRoleName(roleEntity.getName());

        Set<String> permissionNames = rolePermissions.stream()
                .map(RolePermission::getPermissionName)
                .collect(Collectors.toSet());

        List<PermissionEntity> permissions = permissionRepository.findByNameIn(permissionNames);

        return permissions.stream()
                .map(permissionMapper::toPermissionResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<RoleResponse> allRoles(int pageIndex, int pageSize) {
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        Page<RoleEntity> rolePage = roleRepository.findAll(pageable);

        List<RoleResponse> roleResponses = rolePage.getContent().stream()
                .map(this::mapRoleToRoleResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(roleResponses, pageable, rolePage.getTotalElements());
    }

    private RoleResponse mapRoleToRoleResponse(RoleEntity role) {
        List<PermissionResponse> permissions = getPermissionsForRole(role);
        return roleMapper.toRoleResponse(role, permissions);
    }

    private List<PermissionResponse> getPermissionsForRole(RoleEntity role) {
        List<RolePermission> rolePermissions = rolePermissionRepository.findByRoleName(role.getName());
        return rolePermissions.stream()
                .map(this::mapRolePermissionToPermissionResponse)
                .collect(Collectors.toList());
    }

    private PermissionResponse mapRolePermissionToPermissionResponse(RolePermission rolePermission) {
        PermissionEntity permission = permissionRepository.findByName(rolePermission.getPermissionName())
                .orElseThrow(() -> new AppException(ErrorCode.PERMISSION_NOT_FOUND));

        return permissionMapper.toPermissionResponse(permission);
    }

    @Override
    public String deleteRole(String name) {
        RoleEntity roleEntity = roleRepository.findByName(name)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        roleEntity.setDeleted(true);
        roleRepository.save(roleEntity);
        return "Role deleted successfully";
    }
}
