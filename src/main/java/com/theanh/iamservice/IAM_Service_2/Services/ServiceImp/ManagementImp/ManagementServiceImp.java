package com.theanh.iamservice.IAM_Service_2.Services.ServiceImp.ManagementImp;

import com.theanh.iamservice.IAM_Service_2.AuditorAware.AuditorAwareImp;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Management.UserCreationRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Management.UserSearchRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Management.SearchResponse;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Management.UserResponse;
import com.theanh.iamservice.IAM_Service_2.Entities.UserEntity;
import com.theanh.iamservice.IAM_Service_2.Exception.AppException;
import com.theanh.iamservice.IAM_Service_2.Exception.ErrorCode;
import com.theanh.iamservice.IAM_Service_2.Mapper.UserMapper;
import com.theanh.iamservice.IAM_Service_2.Repositories.RepositoryImp.UserRepositoryImp;
import com.theanh.iamservice.IAM_Service_2.Repositories.UserRepository;
import com.theanh.iamservice.IAM_Service_2.Services.IManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ManagementServiceImp implements IManagementService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditorAwareImp auditorAwareImp;
    private final UserMapper userMapper;
    private final UserRepositoryImp userRepositoryImp;

    @Override
    public UserResponse createNewUser(UserCreationRequest userCreationRequest) {
        UserEntity userEntity = userMapper.toUserEntity(userCreationRequest);
        userEntity.setPassword(passwordEncoder.encode(userCreationRequest.getPassword()));

        userEntity.setCreatedBy(auditorAwareImp.getCurrentAuditor().orElse("Unknown"));
        userEntity.setCreatedAt(LocalDateTime.now());

        userEntity.setLastModifiedBy(auditorAwareImp.getCurrentAuditor().orElse("Unknown"));
        userEntity.setLastModifiedAt(LocalDateTime.now());

        userRepository.save(userEntity);

        return userMapper.toUserResponse(userEntity);
    }

    @Override
    public Page<UserResponse> allUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<UserEntity> userPage = userRepository.findAll(pageable);

        return userPage.map(userMapper::toUserResponse);
    }

    @Override
    public UserResponse findUserById(UUID id) {
        return userMapper.toUserResponse(userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND)));
    }

    @Override
    public Page<SearchResponse> findUserByKeyWord(UserSearchRequest userSearchRequest) {
        List<UserEntity> userEntities = userRepositoryImp.search(userSearchRequest);

        Long totalCount = userRepositoryImp.count(userSearchRequest);

        List<SearchResponse> userResponses = userEntities.stream()
                .map(userMapper::toSearchResponse)
                .toList();

        return new PageImpl<>(
                userResponses,
                PageRequest.of(userSearchRequest.getPageIndex() - 1, userSearchRequest.getPageSize()),
                totalCount
        );
    }

    @Override
    public UserResponse banUser(UUID id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        userEntity.setBanned(true);
        userRepository.save(userEntity);
        return userMapper.toUserResponse(userEntity);
    }

    @Override
    public UserResponse deleteUser(UUID id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        userEntity.setDeleted(true);
        userRepository.save(userEntity);
        return userMapper.toUserResponse(userEntity);
    }
}
