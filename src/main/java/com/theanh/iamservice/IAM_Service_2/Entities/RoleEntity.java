package com.theanh.iamservice.IAM_Service_2.Entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "roles")
public class RoleEntity implements GrantedAuthority {
    @Id
    @Column(name = "role_name")
    private String name;

    private boolean isRoot = false;

    private String createdBy;
    private LocalDateTime createdAt;

    private String lastModifiedBy;
    private LocalDateTime lastModifiedAt;

    private boolean isDeleted = false;

    @Override
    public String getAuthority() {
        return name;
    }
}
