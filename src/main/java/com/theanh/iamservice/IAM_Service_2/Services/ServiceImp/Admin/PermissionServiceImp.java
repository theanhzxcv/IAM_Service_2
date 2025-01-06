package com.theanh.iamservice.IAM_Service_2.Services.ServiceImp.Admin;

import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Admin.PermissionCreationRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Admin.PermissionUpdateRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Admin.PermissionResponse;
import com.theanh.iamservice.IAM_Service_2.Entities.PermissionEntity;
import com.theanh.iamservice.IAM_Service_2.Mapper.PermissionMapper;
import com.theanh.iamservice.IAM_Service_2.Repositories.PermissionRepository;
import com.theanh.iamservice.IAM_Service_2.Services.IPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PermissionServiceImp implements IPermissionService {

    private final PermissionMapper permissionMapper;
    private final PermissionRepository permissionRepository;

    @Override
    public PermissionResponse createPermission(PermissionCreationRequest permissionCreationRequest) {
        if (permissionRepository.findByResourceAndScope(permissionCreationRequest.getResource(),
                permissionCreationRequest.getScope()).isPresent()) {
            throw new RuntimeException("Permission exists");
        }

        PermissionEntity permissionEntity = permissionRepository.save(
                permissionMapper.toPermissionEntity(permissionCreationRequest));

        return permissionMapper.toPermissionResponse(permissionEntity);
    }

    @Override
    public PermissionResponse updatePermission(String name, PermissionUpdateRequest permissionUpdateRequest) {
        PermissionEntity permissionEntity = permissionRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Not found"));

        if (permissionUpdateRequest.getResource() != null) {
            permissionEntity.setResource(permissionUpdateRequest.getResource());
        }
        if (permissionUpdateRequest.getScope() != null) {
            permissionEntity.setScope(permissionUpdateRequest.getScope());
        }

        PermissionEntity updatePermission = permissionRepository.save(permissionEntity);

        return permissionMapper.toPermissionResponse(updatePermission);
    }

    @Override
    public Page<PermissionResponse> allPermissions(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PermissionEntity> permissionsPage = permissionRepository.findAll(pageable);

        return permissionsPage.map(permissionMapper::toPermissionResponse);
    }

    @Override
    public String deletePermission(String name) {
        PermissionEntity permissionEntity = permissionRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Not found"));

        permissionEntity.setDeleted(true);
        permissionRepository.save(permissionEntity);
        return "Permission deleted";
    }
}
