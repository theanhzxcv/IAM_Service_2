package com.theanh.iamservice.IAM_Service_2.Mapper;

import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Admin.RoleResponse;
import com.theanh.iamservice.IAM_Service_2.Entities.RoleEntity;
import org.springframework.stereotype.Component;

@Component
public class RoleMapper {

    public RoleResponse toRoleResponse(RoleEntity roleEntity) {
        if (roleEntity == null) {
            throw new IllegalArgumentException("SignUpRequest cannot be null");
        }

        return RoleResponse.builder()
                .id(roleEntity.getId())
                .name(roleEntity.getName())
                .build();
    }
}
