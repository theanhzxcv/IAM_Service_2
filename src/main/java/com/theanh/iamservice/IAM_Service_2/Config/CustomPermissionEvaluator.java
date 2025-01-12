package com.theanh.iamservice.IAM_Service_2.Config;

import com.theanh.iamservice.IAM_Service_2.Entities.*;
import com.theanh.iamservice.IAM_Service_2.Entities.UserDetails.CustomUserDetails;
import com.theanh.iamservice.IAM_Service_2.Exception.AppException;
import com.theanh.iamservice.IAM_Service_2.Exception.ErrorCode;
import com.theanh.iamservice.IAM_Service_2.Repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CustomPermissionEvaluator implements PermissionEvaluator {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication == null
                || !(authentication.getPrincipal() instanceof CustomUserDetails customUserDetails)) {
            return false;
        }

        String email = customUserDetails.getUsername();
        UserEntity userEntity = getUserEntityByEmail(email);

        if (userEntity == null
                || !(targetDomainObject instanceof String resource
                && permission instanceof String scope)) {
            return false;
        }

        return hasPermissionForResourceAndScope(userEntity, resource, scope);
    }

    private UserEntity getUserEntityByEmail(String email) {
        return userRepository.findByEmailAddress(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    private boolean hasPermissionForResourceAndScope(UserEntity userEntity, String resource, String scope) {
        List<UserRole> userRoles = userRoleRepository.findByUserId(userEntity.getId());
        for (UserRole userRole : userRoles) {
            if (isUserRoleRoot(userRole.getRoleName())) {
                return true;
            }
            if (hasMatchingPermission(userRole.getRoleName(), resource, scope)) {
                return true;
            }
        }
        return false;
    }

    private boolean isUserRoleRoot(String roleName) {
        RoleEntity roleEntity = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        return roleEntity.isRoot();
    }

    private boolean hasMatchingPermission(String roleName, String resource, String scope) {
        List<RolePermission> rolePermissions = rolePermissionRepository.findByRoleName(roleName);
        for (RolePermission rolePermission : rolePermissions) {
            if (doesPermissionMatch(rolePermission.getPermissionName(), resource, scope)) {
                return true;
            }
        }
        return false;
    }

    private boolean doesPermissionMatch(String permissionName, String resource, String scope) {
        PermissionEntity permissionEntity = permissionRepository.findByName(permissionName)
                .orElseThrow(() -> new RuntimeException("Permission not found"));

        String permResource = permissionEntity.getResource();
        String permScope = permissionEntity.getScope();

        return permResource.equals(resource) && permScope.equals(scope);
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false;
    }
}
