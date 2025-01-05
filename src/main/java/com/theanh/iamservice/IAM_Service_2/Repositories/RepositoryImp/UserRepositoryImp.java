package com.theanh.iamservice.IAM_Service_2.Repositories.RepositoryImp;

import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Management.UserSearchRequest;
import com.theanh.iamservice.IAM_Service_2.Entities.UserEntity;
import com.theanh.iamservice.IAM_Service_2.Repositories.UserRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserRepositoryImp implements UserRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<UserEntity> search(UserSearchRequest userSearchRequest) {
        Map<String, Object> values = new HashMap<>();
        String sql = "SELECT u FROM UserEntity u "
                + createWhereQuery(userSearchRequest, values);
        Query query = entityManager.createQuery(sql, UserEntity.class);

        values.forEach(query::setParameter);

        query.setFirstResult((userSearchRequest.getPageIndex() - 1) * userSearchRequest.getPageSize());
        query.setMaxResults(userSearchRequest.getPageSize());

        return query.getResultList();
    }

    @Override
    public Long count(UserSearchRequest userSearchRequest) {
        Map<String, Object> values = new HashMap<>();
        String sql = "select count(u) from UserEntity u " +
                createWhereQuery(userSearchRequest, values);
        Query query = entityManager.createQuery(sql, Long.class);

        values.forEach(query::setParameter);

        return (Long) query.getSingleResult();
    }

    private String createWhereQuery(UserSearchRequest request, Map<String, Object> values) {
        StringBuilder sql = new StringBuilder();
        sql.append("where u.isDeleted = false");

        if (request.getKeyword() != null && !request.getKeyword().trim().isEmpty()) {
            String keyword = "%" + request.getKeyword().trim().toLowerCase() + "%";

            sql.append(
                    " AND (LOWER(u.emailAddress) LIKE :keyword "
                            + " OR LOWER(u.username) LIKE :keyword "
                            + " OR LOWER(u.firstname) LIKE :keyword "
                            + " OR LOWER(u.lastname) LIKE :keyword "
                            + " OR LOWER(u.phoneNumber) LIKE :keyword) "
            );
            values.put("keyword", keyword);
        }
        return sql.toString();
    }
}
