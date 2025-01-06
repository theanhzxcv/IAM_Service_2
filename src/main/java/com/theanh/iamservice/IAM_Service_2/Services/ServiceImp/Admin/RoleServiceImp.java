package com.theanh.iamservice.IAM_Service_2.Services.ServiceImp.Admin;

import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Admin.RoleCreationRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Admin.RoleUpdateRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Admin.PermissionResponse;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Admin.RoleResponse;
import com.theanh.iamservice.IAM_Service_2.Entities.PermissionEntity;
import com.theanh.iamservice.IAM_Service_2.Entities.RoleEntity;
import com.theanh.iamservice.IAM_Service_2.Entities.RolePermission;
import com.theanh.iamservice.IAM_Service_2.Mapper.PermissionMapper;
import com.theanh.iamservice.IAM_Service_2.Mapper.RoleMapper;
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

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImp implements IRoleService {
    private final RoleMapper roleMapper;
    private final RoleRepository roleRepository;
    private final PermissionMapper permissionMapper;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;

    @Override
    public RoleResponse createRole(RoleCreationRequest roleCreationRequest) {
        if (roleRepository.findByName(roleCreationRequest.getName()).isPresent()) {
            throw new RuntimeException("Role already exists");
        }

        RoleEntity roleEntity = roleMapper.tpRoleEntity(roleCreationRequest);
        roleRepository.save(roleEntity);

        List<PermissionEntity> permissions =
                permissionRepository.findAllById(roleCreationRequest.getPermissions());

        if (permissions.size() != roleCreationRequest.getPermissions().size()) {
            throw new RuntimeException("Some permissions do not exist");
        }

        List<PermissionEntity> validPermissions = permissions.stream()
                .filter(permission -> !permission.isDeleted())
                .toList();

        if (validPermissions.size() != permissions.size()) {
            throw new RuntimeException("Some permissions are marked as deleted");
        }

        List<RolePermission> rolePermissions = validPermissions.stream()
                .map(permission -> RolePermission.builder()
                        .roleName(roleCreationRequest.getName())
                        .permissionName(permission.getName())
                        .build())
                .collect(Collectors.toList());

        rolePermissionRepository.saveAll(rolePermissions);

        List<PermissionResponse> permissionResponses = validPermissions.stream()
                .map(permissionMapper::toPermissionResponse)
                .collect(Collectors.toList());

        return roleMapper.toRoleResponse(roleEntity, permissionResponses);
    }

    @Override
    public RoleResponse updateRole(String name, RoleUpdateRequest roleUpdateRequest) {
        RoleEntity roleEntity = roleRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Role not found"));

            roleEntity.setDeleted(roleUpdateRequest.isDeleted());

        if (roleUpdateRequest.getPermissions() != null) {
            List<PermissionEntity> permissions =
                    permissionRepository.findAllById(roleUpdateRequest.getPermissions());

            if (permissions.size() != roleUpdateRequest.getPermissions().size()) {
                throw new RuntimeException("Some permissions do not exist");
            }

            List<PermissionEntity> validPermissions = permissions.stream()
                    .filter(permission -> !permission.isDeleted())
                    .toList();

            if (validPermissions.size() != permissions.size()) {
                throw new RuntimeException("Some permissions are marked as deleted");
            }

            List<RolePermission> rolePermissions = validPermissions.stream()
                    .map(permission -> RolePermission.builder()
                            .roleName(roleEntity.getName())
                            .permissionName(permission.getName())
                            .build())
                    .collect(Collectors.toList());

            rolePermissionRepository.saveAll(rolePermissions);
        }

        roleRepository.save(roleEntity);

        List<RolePermission> rolePermissions = rolePermissionRepository.findByRoleName(roleEntity.getName());
        List<PermissionResponse> permissionResponses = rolePermissions.stream()
                .map(rolePermission -> {
                    PermissionEntity permission = permissionRepository.findByName(rolePermission.getPermissionName())
                            .orElseThrow(() -> new RuntimeException("Permission not found"));
                    return new PermissionResponse(
                            permission.getName(),
                            permission.getResource(),
                            permission.getScope(),
                            permission.isDeleted()
                    );
                })
                .collect(Collectors.toList());

        return RoleResponse.builder()
                .name(roleEntity.getName())
                .isDeleted(roleEntity.isDeleted())
                .permissions(permissionResponses)
                .build();
    }

    @Override
    public Page<RoleResponse> allRoles(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<RoleEntity> rolePage = roleRepository.findAll(pageable);

        List<RoleResponse> roleResponses = rolePage.getContent().stream()
                .map(role -> {
                    List<RolePermission> rolePermissions = rolePermissionRepository.findByRoleName(role.getName());
                    List<PermissionResponse> permissions = rolePermissions.stream()
                            .map(rolePermission -> {
                                PermissionEntity permission = permissionRepository.findByName(rolePermission.getPermissionName())
                                        .orElseThrow(() -> new RuntimeException("Permission not found"));
                                return new PermissionResponse(
                                        permission.getName(),
                                        permission.getResource(),
                                        permission.getScope(),
                                        permission.isDeleted()
                                );
                            })
                            .collect(Collectors.toList());

                    return RoleResponse.builder()
                            .name(role.getName())
                            .isDeleted(role.isDeleted())
                            .permissions(permissions)
                            .build();

                })
                .collect(Collectors.toList());

        return new PageImpl<>(roleResponses, pageable, rolePage.getTotalElements());
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
