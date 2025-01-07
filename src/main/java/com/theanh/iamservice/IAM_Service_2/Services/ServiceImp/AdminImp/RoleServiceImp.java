package com.theanh.iamservice.IAM_Service_2.Services.ServiceImp.AdminImp;

import com.theanh.iamservice.IAM_Service_2.AuditorAware.AuditorAwareImp;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Admin.RoleCreationRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Admin.RoleUpdateRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Admin.PermissionResponse;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Admin.RoleResponse;
import com.theanh.iamservice.IAM_Service_2.Entities.PermissionEntity;
import com.theanh.iamservice.IAM_Service_2.Entities.RoleEntity;
import com.theanh.iamservice.IAM_Service_2.Entities.RolePermission;
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
        validateRoleDoesNotExist(roleCreationRequest.getName());

        RoleEntity roleEntity = buildRoleEntity(roleCreationRequest);
        roleRepository.save(roleEntity);

        List<PermissionEntity> validPermissions = fetchAndValidatePermissions(roleCreationRequest.getPermissions());
        saveRolePermissions(roleCreationRequest.getName(), validPermissions);

        return buildRoleResponse(roleEntity, validPermissions);
    }

    private void validateRoleDoesNotExist(String roleName) {
        if (roleRepository.findByName(roleName).isPresent()) {
            throw new RuntimeException("Role already exists");
        }
    }

    private RoleEntity buildRoleEntity(RoleCreationRequest roleCreationRequest) {
        RoleEntity roleEntity = roleMapper.tpRoleEntity(roleCreationRequest);

        if (roleCreationRequest.getName().equalsIgnoreCase("ADMIN")) {
            roleEntity.setRoot(true);
        }

        String currentAuditor = auditorAwareImp.getCurrentAuditor().orElse("Unknown");
        roleEntity.setCreatedBy(currentAuditor);
        roleEntity.setCreatedAt(LocalDateTime.now());
        roleEntity.setLastModifiedBy(currentAuditor);
        roleEntity.setLastModifiedAt(LocalDateTime.now());

        return roleEntity;
    }

    private List<PermissionEntity> fetchAndValidatePermissions(List<String> permissionNames) {
        List<PermissionEntity> permissions = permissionRepository.findAllById(permissionNames);

        if (permissions.size() != permissionNames.size()) {
            throw new RuntimeException("Some permissions do not exist");
        }

        List<PermissionEntity> validPermissions = permissions.stream()
                .filter(permission -> !permission.isDeleted())
                .toList();

        if (validPermissions.size() != permissions.size()) {
            throw new RuntimeException("Some permissions are marked as deleted");
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
        RoleEntity roleEntity = findRoleByName(name);

        updateRoleAttributes(roleEntity, roleUpdateRequest);

        if (roleUpdateRequest.getPermissions() != null) {
            updateRolePermissions(roleEntity, roleUpdateRequest.getPermissions());
        }

        saveRoleAuditDetails(roleEntity);

        List<PermissionResponse> permissionResponses = getPermissionResponsesForRole(roleEntity);

        return roleMapper.toRoleResponse(roleEntity, permissionResponses);
    }

    private RoleEntity findRoleByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Role not found"));
    }

    private void updateRoleAttributes(RoleEntity roleEntity, RoleUpdateRequest roleUpdateRequest) {
        if ("false".equalsIgnoreCase(roleUpdateRequest.getIsDeleted())) {
            roleEntity.setDeleted(false);
        }

        if ("true".equalsIgnoreCase(roleUpdateRequest.getIsRoot())) {
            roleEntity.setRoot(true);
        }
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

    private void saveRoleAuditDetails(RoleEntity roleEntity) {
        String currentAuditor = auditorAwareImp.getCurrentAuditor().orElse("Unknown");
        roleEntity.setLastModifiedBy(currentAuditor);
        roleEntity.setLastModifiedAt(LocalDateTime.now());
        roleRepository.save(roleEntity);
    }

    private List<PermissionResponse> getPermissionResponsesForRole(RoleEntity roleEntity) {
        List<RolePermission> rolePermissions = rolePermissionRepository.findByRoleName(roleEntity.getName());

        return rolePermissions.stream()
                .map(rolePermission -> {
                    PermissionEntity permission = permissionRepository.findByName(rolePermission.getPermissionName())
                            .orElseThrow(() -> new RuntimeException("Permission not found"));
                    return permissionMapper.toPermissionResponse(permission);
                })
                .collect(Collectors.toList());
    }

    @Override
    public Page<RoleResponse> allRoles(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RoleEntity> rolePage = roleRepository.findAll(pageable);

        List<RoleResponse> roleResponses = rolePage.getContent().stream()
                .map(this::mapRoleToRoleResponse)  // Using method reference for clarity
                .collect(Collectors.toList());

        return new PageImpl<>(roleResponses, pageable, rolePage.getTotalElements());
    }

    private RoleResponse mapRoleToRoleResponse(RoleEntity role) {
        List<PermissionResponse> permissions = getPermissionsForRole(role);
        return RoleResponse.builder()
                .name(role.getName())
                .isRoot(role.isRoot())
                .isDeleted(role.isDeleted())
                .permissions(permissions)
                .build();
    }

    private List<PermissionResponse> getPermissionsForRole(RoleEntity role) {
        List<RolePermission> rolePermissions = rolePermissionRepository.findByRoleName(role.getName());
        return rolePermissions.stream()
                .map(this::mapRolePermissionToPermissionResponse)
                .collect(Collectors.toList());
    }

    private PermissionResponse mapRolePermissionToPermissionResponse(RolePermission rolePermission) {
        PermissionEntity permission = permissionRepository.findByName(rolePermission.getPermissionName())
                .orElseThrow(() -> new RuntimeException("Permission not found"));

        return PermissionResponse.builder()
                .name(permission.getName())
                .resource(permission.getResource())
                .scope(permission.getScope())
                .isDeleted(permission.isDeleted())
                .build();
    }

    @Override
    public String deleteRole(String name) {
        RoleEntity roleEntity = roleRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Not found"));

        roleEntity.setDeleted(true);
        roleRepository.save(roleEntity);
        return "Role deleted";
    }
}
