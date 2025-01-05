package com.theanh.iamservice.IAM_Service_2.Repositories;

import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Management.UserSearchRequest;
import com.theanh.iamservice.IAM_Service_2.Entities.UserEntity;

import java.util.List;

public interface UserRepositoryCustom {

    List<UserEntity> search(UserSearchRequest userSearchRequest);

    Long count(UserSearchRequest userSearchRequest);
}
