package com.theanh.iamservice.IAM_Service_2.Repositories;

import com.theanh.iamservice.IAM_Service_2.Entities.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PermissionRepository extends JpaRepository<PermissionEntity, Long> {
    Optional<PermissionEntity> findByResourceAndScope(String resource, String scope);
}
