package com.theanh.iamservice.IAM_Service_2.Repositories;

import com.theanh.iamservice.IAM_Service_2.Entities.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface PermissionRepository extends JpaRepository<PermissionEntity, String> {
    Optional<PermissionEntity> findByResourceAndScope(String resource, String scope);

    Optional<PermissionEntity> findByName(String name);
}
