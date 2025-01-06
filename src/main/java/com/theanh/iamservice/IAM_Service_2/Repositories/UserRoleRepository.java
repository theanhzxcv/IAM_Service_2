package com.theanh.iamservice.IAM_Service_2.Repositories;

import com.theanh.iamservice.IAM_Service_2.Entities.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
}
