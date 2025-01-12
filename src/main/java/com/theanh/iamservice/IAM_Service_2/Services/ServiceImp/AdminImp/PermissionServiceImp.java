package com.theanh.iamservice.IAM_Service_2.Services.ServiceImp.AdminImp;

import com.theanh.iamservice.IAM_Service_2.AuditorAware.AuditorAwareImp;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Admin.PermissionCreationRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Admin.PermissionUpdateRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Admin.PermissionResponse;
import com.theanh.iamservice.IAM_Service_2.Entities.PermissionEntity;
import com.theanh.iamservice.IAM_Service_2.Exception.AppException;
import com.theanh.iamservice.IAM_Service_2.Exception.ErrorCode;
import com.theanh.iamservice.IAM_Service_2.Mappers.PermissionMapper;
import com.theanh.iamservice.IAM_Service_2.Repositories.PermissionRepository;
import com.theanh.iamservice.IAM_Service_2.Services.IPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PermissionServiceImp implements IPermissionService {
    private final AuditorAwareImp auditorAwareImp;
    private final PermissionMapper permissionMapper;
    private final PermissionRepository permissionRepository;

    @Override
    public PermissionResponse createPermission(PermissionCreationRequest permissionCreationRequest) {
        if (permissionRepository.findByResourceAndScope(permissionCreationRequest.getResource(),
                permissionCreationRequest.getScope()).isPresent()) {
            throw new AppException(ErrorCode.PERMISSION_ALREADY_EXISTS);
        }

        PermissionEntity permissionEntity = permissionMapper.toPermissionEntity(permissionCreationRequest);
        permissionEntity.setCreatedBy(auditorAwareImp.getCurrentAuditor().orElse("Unknown"));
        permissionEntity.setCreatedAt(LocalDateTime.now());
        permissionEntity.setLastModifiedBy(auditorAwareImp.getCurrentAuditor().orElse("Unknown"));
        permissionEntity.setLastModifiedAt(LocalDateTime.now());
        permissionRepository.save(permissionEntity);

        return permissionMapper.toPermissionResponse(permissionEntity);
    }

    @Override
    public PermissionResponse updatePermission(String name, PermissionUpdateRequest permissionUpdateRequest) {
        PermissionEntity permissionEntity = permissionRepository.findByName(name)
                .orElseThrow(() -> new AppException(ErrorCode.PERMISSION_NOT_FOUND));

        if (permissionUpdateRequest == null) {
            throw new AppException(ErrorCode.FIELD_MISSING);
        }

        permissionEntity.setResource(permissionUpdateRequest.getResource());
        permissionEntity.setScope(permissionUpdateRequest.getScope());
        permissionEntity.setDeleted(!"No".equalsIgnoreCase(permissionUpdateRequest.getIsDeleted()));
        permissionEntity.setLastModifiedBy(auditorAwareImp.getCurrentAuditor().orElse("Unknown"));
        permissionEntity.setLastModifiedAt(LocalDateTime.now());

        PermissionEntity updatedPermission = permissionRepository.save(permissionEntity);
        return permissionMapper.toPermissionResponse(updatedPermission);
    }

    @Override
    public Page<PermissionResponse> allPermissions(int pageIndex, int pageSize) {
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        Page<PermissionEntity> permissionsPage = permissionRepository.findAll(pageable);

        return permissionsPage.map(permissionMapper::toPermissionResponse);
    }

    @Override
    public String deletePermission(String name) {
        PermissionEntity permissionEntity = permissionRepository.findByName(name)
                .orElseThrow(() -> new AppException(ErrorCode.PERMISSION_NOT_FOUND));

        permissionEntity.setDeleted(true);
        permissionRepository.save(permissionEntity);
        return "Permission deleted successfully";
    }
}
