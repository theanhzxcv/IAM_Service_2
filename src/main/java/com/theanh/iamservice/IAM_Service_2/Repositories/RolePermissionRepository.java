package com.theanh.iamservice.IAM_Service_2.Repositories;

import com.theanh.iamservice.IAM_Service_2.Entities.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {
    List<RolePermission> findByRoleName(String roleName);

    void deleteByRoleName(String name);

}
