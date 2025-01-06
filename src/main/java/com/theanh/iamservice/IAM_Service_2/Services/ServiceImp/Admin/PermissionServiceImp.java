package com.theanh.iamservice.IAM_Service_2.Services.ServiceImp.Admin;

import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Admin.PermissionRequest;
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
    public PermissionResponse createPermission(PermissionRequest permissionRequest) {
        if (permissionRepository.findByResourceAndScope(permissionRequest.getResource(),
                permissionRequest.getScope()).isPresent()) {
            throw new RuntimeException("Permission exists");
        }

        PermissionEntity permissionEntity = permissionRepository.save(
                permissionMapper.toPermissionEntity(permissionRequest));

        return permissionMapper.toPermissionResponse(permissionEntity);
    }

    @Override
    public PermissionResponse updatePermission(Long id, PermissionRequest permissionRequest) {
        PermissionEntity permissionEntity = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));

        if (permissionRequest.getResource() != null) {
            permissionEntity.setResource(permissionRequest.getResource());
        }
        if (permissionRequest.getScope() != null) {
            permissionEntity.setScope(permissionRequest.getScope());
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
    public String deletePermission(Long id) {
        PermissionEntity permissionEntity = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));

        permissionEntity.setDeleted(true);
        permissionRepository.save(permissionEntity);
        return "Permission deleted";
    }
}
