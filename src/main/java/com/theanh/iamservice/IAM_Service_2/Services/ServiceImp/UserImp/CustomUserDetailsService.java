package com.theanh.iamservice.IAM_Service_2.Services.ServiceImp.UserImp;

import com.theanh.iamservice.IAM_Service_2.Entities.PermissionEntity;
import com.theanh.iamservice.IAM_Service_2.Entities.RolePermission;
import com.theanh.iamservice.IAM_Service_2.Entities.UserDetails.CustomUserDetails;
import com.theanh.iamservice.IAM_Service_2.Entities.UserEntity;
import com.theanh.iamservice.IAM_Service_2.Entities.UserRole;
import com.theanh.iamservice.IAM_Service_2.Repositories.PermissionRepository;
import com.theanh.iamservice.IAM_Service_2.Repositories.RolePermissionRepository;
import com.theanh.iamservice.IAM_Service_2.Repositories.UserRepository;
import com.theanh.iamservice.IAM_Service_2.Repositories.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmailAddress(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Collection<? extends GrantedAuthority> authorities = getAuthoritiesForUser(userEntity);

        return new CustomUserDetails(userEntity, authorities);
    }

    private Collection<? extends GrantedAuthority> getAuthoritiesForUser(UserEntity userEntity) {
        List<UserRole> userRoles = userRoleRepository.findByUserId(userEntity.getId());

        return userRoles.stream()
                .flatMap(userRole -> {
                    List<RolePermission> rolePermissions = rolePermissionRepository.findByRoleName(userRole.getRoleName());

                    return rolePermissions.stream()
                            .map(rolePermission -> {
                                PermissionEntity permission = permissionRepository.findByName(rolePermission.getPermissionName())
                                        .orElseThrow(() -> new RuntimeException("Permission not found"));
                                return new SimpleGrantedAuthority(permission.getResource() + ":" + permission.getScope());
                            });
                })
                .collect(Collectors.toSet());
    }
}
